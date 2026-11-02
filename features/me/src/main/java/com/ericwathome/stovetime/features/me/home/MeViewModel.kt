package com.ericwathome.stovetime.features.me.home

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.helpers.Today
import com.ericwathome.stovetime.core.data.helpers.asLocalDate
import com.ericwathome.stovetime.core.data.model.progress.CookKind
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
}

sealed interface MeEvent {
    data class OpenLesson(
        val lessonId: String,
    ) : MeEvent

    data class OpenRecipe(
        val recipeId: String,
    ) : MeEvent

    data object OpenPath : MeEvent
}

data class MeState(
    val progress: ViewState<Progress> = ViewState.Initial,
    val today: LocalDate = Today,
) {
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
) : MviViewModel<MeAction, MeState, MeEvent>(MeState()) {
    private var observeJob: Job? = null

    override fun onStateSubscription() {
        if (observeJob == null) observeProgress()
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
