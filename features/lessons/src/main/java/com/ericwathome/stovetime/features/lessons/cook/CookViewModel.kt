package com.ericwathome.stovetime.features.lessons.cook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.cook.CookStep
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.core.data.repo.cook.CookRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.data.repo.settings.TimerPromptsRepository
import com.ericwathome.stovetime.core.data.repo.timers.TimersRepository
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.dataOrNull
import kotlinx.coroutines.launch

private const val STEP_KEY = "cook_step"

data class CookArgs(
    val kind: CookKind,
    val dishId: String,
    val startedAt: Long,
    val step: Int,
)

sealed interface CookAction {
    data object OnClickNext : CookAction

    data object OnClickBack : CookAction

    data object OnClickClose : CookAction

    data class OnConfirmLeave(
        val keepTimers: Boolean?,
    ) : CookAction

    data class OnClickStartTimer(
        val name: String,
        val notificationsAllowed: Boolean,
    ) : CookAction

    data object OnNotificationsExplained : CookAction

    data object OnNotificationPermissionResult : CookAction

    data class OnClickPauseTimer(
        val timerId: String,
    ) : CookAction

    data class OnClickResumeTimer(
        val timerId: String,
    ) : CookAction

    data class OnClickResetTimer(
        val timerId: String,
    ) : CookAction

    data class OnClickStopTimer(
        val timerId: String,
    ) : CookAction

    data class OnClickTimerChip(
        val step: Int,
    ) : CookAction

    data class OnConfirmFinish(
        val keepTimers: Boolean?,
    ) : CookAction

    data object OnClickRetry : CookAction
}

sealed interface CookEvent {
    data object AskToLeave : CookEvent

    data object AskToKeepTimersOnLeave : CookEvent

    data object AskToKeepTimersOnFinish : CookEvent

    data object ExplainNotifications : CookEvent

    data object RequestNotificationPermission : CookEvent

    data object ExplainLateAlarms : CookEvent

    data object ExplainLockScreenAlarms : CookEvent

    data object Close : CookEvent

    data class Finish(
        val kind: CookKind,
        val dishId: String,
        val startedAt: Long,
        val finishedAt: Long,
    ) : CookEvent
}

data class CookState(
    val plan: ViewState<CookPlan> = ViewState.Initial,
    val step: Int = 0,
    val timers: List<RunningTimer> = emptyList(),
    val nowMillis: Long = 0,
    val keepScreenOn: Boolean = true,
) {
    val steps: List<CookStep>
        get() = plan.dataOrNull()?.steps.orEmpty()

    val currentStep: CookStep?
        get() = steps.getOrNull(step)

    val isLastStep: Boolean
        get() = steps.isNotEmpty() && step == steps.lastIndex

    val progress: Float
        get() = if (steps.isEmpty()) 0f else (step + 1).toFloat() / steps.size

    val currentTimer: RunningTimer?
        get() = timerFor(step)

    val otherTimers: List<RunningTimer>
        get() = timers.filter { stepOf(it) != step }

    fun timerFor(step: Int): RunningTimer? = timers.firstOrNull { stepOf(it) == step }

    fun stepOf(timer: RunningTimer): Int? = (timer.target as? TimerTarget.CookStep)?.step
}

class CookViewModel(
    private val args: CookArgs,
    private val savedStateHandle: SavedStateHandle,
    private val cookRepository: CookRepository,
    private val settingsRepository: SettingsRepository,
    private val timersRepository: TimersRepository,
    private val timerPromptsRepository: TimerPromptsRepository,
) : MviViewModel<CookAction, CookState, CookEvent>(
        CookState(step = savedStateHandle[STEP_KEY] ?: args.step),
    ) {
    private var started = false
    private var pendingTimerName: String? = null

    override fun onStateSubscription() {
        if (started) return
        started = true
        loadPlan()
        observeTimers()
        observeSettings()
    }

    override fun onAction(action: CookAction) {
        when (action) {
            CookAction.OnClickNext -> {
                onClickNext()
            }

            CookAction.OnClickBack -> {
                onClickBack()
            }

            CookAction.OnClickClose -> {
                sendEvent { CookEvent.AskToLeave }
            }

            is CookAction.OnConfirmLeave -> {
                onConfirmLeave(action.keepTimers)
            }

            is CookAction.OnClickStartTimer -> {
                onClickStartTimer(action.name, action.notificationsAllowed)
            }

            CookAction.OnNotificationsExplained -> {
                sendEvent { CookEvent.RequestNotificationPermission }
            }

            CookAction.OnNotificationPermissionResult -> {
                pendingTimerName?.let { startTimer(it) }
            }

            is CookAction.OnClickPauseTimer -> {
                timersRepository.pause(action.timerId)
            }

            is CookAction.OnClickResumeTimer -> {
                timersRepository.resume(action.timerId)
            }

            is CookAction.OnClickResetTimer -> {
                timersRepository.cancel(action.timerId)
            }

            is CookAction.OnClickStopTimer -> {
                timersRepository.stopRinging(action.timerId)
            }

            is CookAction.OnClickTimerChip -> {
                goTo(action.step)
            }

            is CookAction.OnConfirmFinish -> {
                onConfirmFinish(action.keepTimers)
            }

            CookAction.OnClickRetry -> {
                loadPlan()
            }
        }
    }

    private fun loadPlan() {
        viewModelScope.launch {
            updateState { it.copy(plan = ViewState.Loading) }
            cookRepository
                .getCookPlan(args.kind, args.dishId)
                .onSuccess { plan ->
                    updateState { it.copy(plan = ViewState.Success(plan), step = it.step.coerceIn(0, (plan.steps.size - 1).coerceAtLeast(0))) }
                }.onError { error -> updateState { it.copy(plan = ViewState.Error(error)) } }
        }
    }

    private fun observeTimers() {
        viewModelScope.launch {
            timersRepository.observeTimers().collect { all ->
                val mine = all.filter { it.belongsToThisCook() }
                updateState { it.copy(timers = mine, nowMillis = Now.toEpochMilliseconds()) }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeCookSettings().collect { settings ->
                updateState { it.copy(keepScreenOn = settings.keepScreenOn) }
            }
        }
    }

    private fun RunningTimer.belongsToThisCook(): Boolean {
        val target = target as? TimerTarget.CookStep ?: return false
        return target.kind == args.kind && target.dishId == args.dishId && target.startedAt == args.startedAt
    }

    private fun goTo(step: Int) {
        val last = currentState.steps.lastIndex
        if (last < 0) return
        val target = step.coerceIn(0, last)
        savedStateHandle[STEP_KEY] = target
        updateState { it.copy(step = target) }
    }

    private fun onClickNext() {
        if (currentState.plan !is ViewState.Success) return
        if (currentState.isLastStep) {
            if (currentState.timers.isNotEmpty()) {
                sendEvent { CookEvent.AskToKeepTimersOnFinish }
            } else {
                finish()
            }
        } else {
            goTo(currentState.step + 1)
        }
    }

    private fun onClickBack() {
        if (currentState.step == 0 || currentState.plan !is ViewState.Success) {
            sendEvent { CookEvent.AskToLeave }
        } else {
            goTo(currentState.step - 1)
        }
    }

    private fun onConfirmLeave(keepTimers: Boolean?) {
        when {
            keepTimers == null && currentState.timers.isNotEmpty() -> {
                sendEvent { CookEvent.AskToKeepTimersOnLeave }
            }

            else -> {
                if (keepTimers == false) cancelMyTimers()
                sendEvent { CookEvent.Close }
            }
        }
    }

    private fun onConfirmFinish(keepTimers: Boolean?) {
        if (keepTimers == false) cancelMyTimers()
        finish()
    }

    private fun cancelMyTimers() {
        currentState.timers.forEach { timersRepository.cancel(it.id) }
    }

    private fun finish() {
        sendEvent {
            CookEvent.Finish(
                kind = args.kind,
                dishId = args.dishId,
                startedAt = args.startedAt,
                finishedAt = Now.toEpochMilliseconds(),
            )
        }
    }

    private fun onClickStartTimer(
        name: String,
        notificationsAllowed: Boolean,
    ) {
        if (currentState.currentTimer != null) return
        viewModelScope.launch {
            if (!notificationsAllowed && !timerPromptsRepository.wasNotificationsExplained()) {
                pendingTimerName = name
                sendEvent { CookEvent.ExplainNotifications }
                timerPromptsRepository.markNotificationsExplained()
            } else {
                startTimer(name)
            }
        }
    }

    private fun startTimer(name: String) {
        pendingTimerName = null
        val step = currentState.step
        val cookStep = currentState.currentStep ?: return
        val seconds = cookStep.timerSeconds ?: return
        if (currentState.timerFor(step) != null) return
        timersRepository.start(
            name = name,
            durationMillis = seconds * 1_000L,
            detail = cookStep.doneness,
            target = TimerTarget.CookStep(kind = args.kind, dishId = args.dishId, startedAt = args.startedAt, step = step),
        )
        viewModelScope.launch {
            when {
                !timersRepository.exactAlarmsAllowed && !timerPromptsRepository.wasLateAlarmsExplained() -> {
                    sendEvent { CookEvent.ExplainLateAlarms }
                    timerPromptsRepository.markLateAlarmsExplained()
                }

                !timersRepository.fullScreenAlarmsAllowed && !timerPromptsRepository.wasLockScreenAlarmsExplained() -> {
                    sendEvent { CookEvent.ExplainLockScreenAlarms }
                    timerPromptsRepository.markLockScreenAlarmsExplained()
                }
            }
        }
    }
}
