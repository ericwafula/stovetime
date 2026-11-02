package com.ericwathome.stovetime.features.lessons.finish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.common.result.getOrNull
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.progress.NewCook
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.core.data.repo.cook.CookRepository
import com.ericwathome.stovetime.core.data.repo.progress.ProgressRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val COOK_ID_KEY = "finish_cook_id"
private const val XP_KEY = "finish_xp"

data class FinishArgs(
    val kind: CookKind,
    val dishId: String,
    val startedAt: Long,
    val finishedAt: Long,
)

data class FinishedDish(
    val name: String,
    val photo: String?,
    val xpEarned: Int,
    val cookMillis: Long,
)

sealed interface FinishAction {
    data class OnSelectRating(
        val rating: Rating,
    ) : FinishAction

    data object OnClickContinue : FinishAction

    data object OnClickRetry : FinishAction
}

sealed interface FinishEvent {
    data object Continue : FinishEvent
}

data class FinishState(
    val dish: ViewState<FinishedDish> = ViewState.Initial,
    val progress: Progress? = null,
    val rating: Rating? = null,
) {
    val goalJustMet: Boolean
        get() = progress?.thisWeek?.let { it.cooks == it.goal } == true
}

class FinishViewModel(
    private val args: FinishArgs,
    private val savedStateHandle: SavedStateHandle,
    private val cookRepository: CookRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val progressUseCases: ProgressUseCases,
) : MviViewModel<FinishAction, FinishState, FinishEvent>(FinishState()) {
    private var started = false

    override fun onStateSubscription() {
        if (started) return
        started = true
        finishCook()
        observeProgress()
    }

    override fun onAction(action: FinishAction) {
        when (action) {
            is FinishAction.OnSelectRating -> {
                onSelectRating(action.rating)
            }

            FinishAction.OnClickContinue -> {
                sendEvent { FinishEvent.Continue }
            }

            FinishAction.OnClickRetry -> {
                finishCook()
            }
        }
    }

    private fun finishCook() {
        viewModelScope.launch {
            updateState { it.copy(dish = ViewState.Loading) }
            cookRepository
                .getCookPlan(args.kind, args.dishId)
                .onSuccess { plan -> saveOnce(plan) }
                .onError { error -> updateState { it.copy(dish = ViewState.Error(error)) } }
        }
    }

    private suspend fun saveOnce(plan: CookPlan) {
        val savedXp: Int? = savedStateHandle[XP_KEY]
        if (savedXp != null) {
            showDish(plan, savedXp)
            return
        }
        val history = progressUseCases.observeProgress().first().getOrNull()?.history.orEmpty()
        val cookedBefore = history.any { it.kind == args.kind && it.dishId == args.dishId }
        val xp = progressUseCases.calculateCookXp(args.kind, plan.baseXp, cookedBefore)
        val goal = settingsRepository.observeCookSettings().first().weeklyGoal
        val result =
            progressRepository.saveCook(
                NewCook(
                    dishId = args.dishId,
                    kind = args.kind,
                    name = plan.name,
                    photo = plan.photo,
                    startedAt = args.startedAt,
                    finishedAt = args.finishedAt,
                    xp = xp,
                    weeklyGoal = goal,
                ),
            )
        when (result) {
            is DataResult.Success -> {
                savedStateHandle[COOK_ID_KEY] = result.data
                savedStateHandle[XP_KEY] = xp
                showDish(plan, xp)
            }

            is DataResult.Error -> {
                updateState { it.copy(dish = ViewState.Error(result.error)) }
            }
        }
    }

    private fun showDish(
        plan: CookPlan,
        xp: Int,
    ) {
        val dish =
            FinishedDish(
                name = plan.name,
                photo = plan.photo,
                xpEarned = xp,
                cookMillis = (args.finishedAt - args.startedAt).coerceAtLeast(0),
            )
        updateState { it.copy(dish = ViewState.Success(dish)) }
    }

    private fun observeProgress() {
        viewModelScope.launch {
            progressUseCases.observeProgress().collect { result ->
                result.onSuccess { progress -> updateState { it.copy(progress = progress) } }
            }
        }
    }

    private fun onSelectRating(rating: Rating) {
        val cookId: Long = savedStateHandle[COOK_ID_KEY] ?: return
        val previous = currentState.rating
        updateState { it.copy(rating = rating) }
        viewModelScope.launch {
            progressRepository.rateCook(cookId, rating).onError {
                updateState { state -> if (state.rating == rating) state.copy(rating = previous) else state }
            }
        }
    }
}
