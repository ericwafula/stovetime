package com.ericwathome.stovetime.features.me.home

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.helpers.Today
import com.ericwathome.stovetime.core.data.helpers.asLocalDate
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.dataOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

sealed interface MeAction {
    data class OnClickCook(
        val cookId: Long,
    ) : MeAction

    data object OnClickGoToPath : MeAction

    data object OnClickRetry : MeAction

    data object OnClickLowerGoal : MeAction

    data object OnClickRaiseGoal : MeAction

    data class OnKeepScreenOnChange(
        val keepScreenOn: Boolean,
    ) : MeAction
}

sealed interface MeEvent {
    data class OpenLesson(
        val lessonId: String,
    ) : MeEvent

    data class OpenRecipe(
        val recipeId: String,
    ) : MeEvent

    data object OpenPath : MeEvent

    data object ShowSaveFailed : MeEvent
}

data class MeState(
    val progress: ViewState<Progress> = ViewState.Initial,
    val today: LocalDate = Today,
    val settings: CookSettings? = null,
) {
    val canLowerGoal: Boolean
        get() = settings != null && settings.weeklyGoal > CookSettings.MIN_WEEKLY_GOAL

    val canRaiseGoal: Boolean
        get() = settings != null && settings.weeklyGoal < CookSettings.MAX_WEEKLY_GOAL

    val cookingSince: LocalDate?
        get() =
            progress
                .dataOrNull()
                ?.history
                ?.minOfOrNull { it.finishedAt }
                ?.asLocalDate
}

class MeViewModel(
    private val progressUseCases: ProgressUseCases,
    private val settingsRepository: SettingsRepository,
) : MviViewModel<MeAction, MeState, MeEvent>(MeState()) {
    private var observeJob: Job? = null
    private var settingsJob: Job? = null

    override fun onStateSubscription() {
        if (observeJob == null) observeProgress()
        if (settingsJob == null) observeSettings()
    }

    override fun onAction(action: MeAction) {
        when (action) {
            is MeAction.OnClickCook -> {
                onClickCook(action.cookId)
            }

            MeAction.OnClickGoToPath -> {
                sendEvent { MeEvent.OpenPath }
            }

            MeAction.OnClickRetry -> {
                observeProgress()
            }

            MeAction.OnClickLowerGoal -> {
                onChangeGoal(by = -1)
            }

            MeAction.OnClickRaiseGoal -> {
                onChangeGoal(by = 1)
            }

            is MeAction.OnKeepScreenOnChange -> {
                onKeepScreenOnChange(action.keepScreenOn)
            }
        }
    }

    private fun observeProgress() {
        observeJob?.cancel()
        updateState { it.copy(progress = ViewState.Loading) }
        observeJob =
            viewModelScope.launch {
                progressUseCases.observeProgress().collect { result ->
                    result
                        .onSuccess { progress -> updateState { it.copy(progress = ViewState.Success(progress), today = Today) } }
                        .onError { error -> updateState { it.copy(progress = ViewState.Error(error)) } }
                }
            }
    }

    private fun observeSettings() {
        settingsJob =
            viewModelScope.launch {
                settingsRepository.observeCookSettings().collect { settings ->
                    updateState { it.copy(settings = settings) }
                }
            }
    }

    private fun onChangeGoal(by: Int) {
        val settings = currentState.settings ?: return
        val goal = (settings.weeklyGoal + by).coerceIn(CookSettings.MIN_WEEKLY_GOAL, CookSettings.MAX_WEEKLY_GOAL)
        if (goal == settings.weeklyGoal) return
        updateState { it.copy(settings = settings.copy(weeklyGoal = goal)) }
        viewModelScope.launch {
            settingsRepository.saveWeeklyGoal(goal).onError { onSaveFailed(settings) }
        }
    }

    private fun onKeepScreenOnChange(keepScreenOn: Boolean) {
        val settings = currentState.settings ?: return
        updateState { it.copy(settings = settings.copy(keepScreenOn = keepScreenOn)) }
        viewModelScope.launch {
            settingsRepository.saveKeepScreenOn(keepScreenOn).onError { onSaveFailed(settings) }
        }
    }

    private fun onSaveFailed(previous: CookSettings) {
        updateState { it.copy(settings = previous) }
        sendEvent { MeEvent.ShowSaveFailed }
    }

    private fun onClickCook(cookId: Long) {
        val cook = currentState.progress.dataOrNull()?.history?.firstOrNull { it.id == cookId } ?: return
        sendEvent {
            when (cook.kind) {
                CookKind.Lesson -> MeEvent.OpenLesson(cook.dishId)
                CookKind.Recipe -> MeEvent.OpenRecipe(cook.dishId)
            }
        }
    }
}
