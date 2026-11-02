package com.ericwathome.stovetime.features.timers.timers

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.core.data.repo.settings.TimerPromptsRepository
import com.ericwathome.stovetime.core.data.repo.timers.TimersRepository
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.launchers.quickTimer.QuickTimerForm
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.launch

private val SHORTEST_QUICK_TIMER = 10.seconds
private val LONGEST_QUICK_TIMER = 180.minutes

sealed interface TimersAction {
    data class OnClickPause(
        val timerId: String,
    ) : TimersAction

    data class OnClickResume(
        val timerId: String,
    ) : TimersAction

    data class OnClickAddMinute(
        val timerId: String,
    ) : TimersAction

    data class OnClickCancel(
        val timerId: String,
    ) : TimersAction

    data class OnClickStop(
        val timerId: String,
    ) : TimersAction

    data class OnClickTimer(
        val timerId: String,
    ) : TimersAction

    data object OnClickNewQuickTimer : TimersAction

    data class OnQuickTimerNameChange(
        val name: String,
    ) : TimersAction

    data class OnQuickTimerAdjust(
        val deltaSeconds: Int,
    ) : TimersAction

    data class OnClickStartQuickTimer(
        val defaultNameFormat: String,
        val notificationsAllowed: Boolean,
    ) : TimersAction

    data object OnNotificationsExplained : TimersAction

    data object OnNotificationPermissionResult : TimersAction
}

sealed interface TimersEvent {
    data object OpenQuickTimer : TimersEvent

    data object ExplainNotifications : TimersEvent

    data object RequestNotificationPermission : TimersEvent

    data object ExplainLateAlarms : TimersEvent

    data object ExplainLockScreenAlarms : TimersEvent

    data class OpenCookStep(
        val kind: CookKind,
        val dishId: String,
        val startedAt: Long,
        val step: Int,
    ) : TimersEvent
}

data class TimersState(
    val timers: List<RunningTimer> = emptyList(),
    val nowMillis: Long = 0,
    val quickTimer: QuickTimerForm = QuickTimerForm(),
) {
    val isEmpty: Boolean
        get() = timers.isEmpty()

    val isTicking: Boolean
        get() = timers.any { !it.isPaused && !it.ringing }
}

private data class PendingQuickTimer(
    val name: String,
    val durationMillis: Long,
)

class TimersViewModel(
    private val timersRepository: TimersRepository,
    private val timerPromptsRepository: TimerPromptsRepository,
) : MviViewModel<TimersAction, TimersState, TimersEvent>(TimersState()) {
    private var started = false
    private var pendingQuickTimer: PendingQuickTimer? = null

    override fun onStateSubscription() {
        if (started) return
        started = true
        viewModelScope.launch {
            timersRepository.observeTimers().collect { timers ->
                updateState { it.copy(timers = timers.sortedWith(listOrder), nowMillis = Now.toEpochMilliseconds()) }
            }
        }
    }

    override fun onAction(action: TimersAction) {
        when (action) {
            is TimersAction.OnClickPause -> {
                timersRepository.pause(action.timerId)
            }

            is TimersAction.OnClickResume -> {
                timersRepository.resume(action.timerId)
            }

            is TimersAction.OnClickAddMinute -> {
                timersRepository.addMinute(action.timerId)
            }

            is TimersAction.OnClickCancel -> {
                timersRepository.cancel(action.timerId)
            }

            is TimersAction.OnClickStop -> {
                timersRepository.stopRinging(action.timerId)
            }

            is TimersAction.OnClickTimer -> {
                onClickTimer(action.timerId)
            }

            TimersAction.OnClickNewQuickTimer -> {
                onClickNewQuickTimer()
            }

            is TimersAction.OnQuickTimerNameChange -> {
                updateState { it.copy(quickTimer = it.quickTimer.copy(name = action.name)) }
            }

            is TimersAction.OnQuickTimerAdjust -> {
                onQuickTimerAdjust(action.deltaSeconds)
            }

            is TimersAction.OnClickStartQuickTimer -> {
                onClickStartQuickTimer(action.defaultNameFormat, action.notificationsAllowed)
            }

            TimersAction.OnNotificationsExplained -> {
                sendEvent { TimersEvent.RequestNotificationPermission }
            }

            TimersAction.OnNotificationPermissionResult -> {
                pendingQuickTimer?.let { startQuickTimer(it) }
            }
        }
    }

    private fun onClickTimer(timerId: String) {
        val target = currentState.timers.firstOrNull { it.id == timerId }?.target as? TimerTarget.CookStep ?: return
        sendEvent {
            TimersEvent.OpenCookStep(kind = target.kind, dishId = target.dishId, startedAt = target.startedAt, step = target.step)
        }
    }

    private fun onClickNewQuickTimer() {
        updateState { it.copy(quickTimer = QuickTimerForm()) }
        sendEvent { TimersEvent.OpenQuickTimer }
    }

    private fun onQuickTimerAdjust(deltaSeconds: Int) {
        val shortest = SHORTEST_QUICK_TIMER.inWholeSeconds.toInt()
        val longest = LONGEST_QUICK_TIMER.inWholeSeconds.toInt()
        updateState {
            val seconds = (it.quickTimer.durationSeconds + deltaSeconds).coerceIn(shortest, longest)
            it.copy(quickTimer = it.quickTimer.copy(durationSeconds = seconds))
        }
    }

    private fun onClickStartQuickTimer(
        defaultNameFormat: String,
        notificationsAllowed: Boolean,
    ) {
        val form = currentState.quickTimer
        val quickTimer =
            PendingQuickTimer(
                name = form.name.trim().ifEmpty { nextDefaultName(defaultNameFormat) },
                durationMillis = form.durationSeconds.seconds.inWholeMilliseconds,
            )
        viewModelScope.launch {
            if (!notificationsAllowed && !timerPromptsRepository.wasNotificationsExplained()) {
                pendingQuickTimer = quickTimer
                sendEvent { TimersEvent.ExplainNotifications }
                timerPromptsRepository.markNotificationsExplained()
            } else {
                startQuickTimer(quickTimer)
            }
        }
    }

    private fun nextDefaultName(format: String): String {
        val taken = currentState.timers.map { it.name }.toSet()
        return generateSequence(1) { it + 1 }.map { format.format(it) }.first { it !in taken }
    }

    private fun startQuickTimer(quickTimer: PendingQuickTimer) {
        pendingQuickTimer = null
        timersRepository.start(name = quickTimer.name, durationMillis = quickTimer.durationMillis, detail = null, target = TimerTarget.Quick)
        viewModelScope.launch {
            when {
                !timersRepository.exactAlarmsAllowed && !timerPromptsRepository.wasLateAlarmsExplained() -> {
                    sendEvent { TimersEvent.ExplainLateAlarms }
                    timerPromptsRepository.markLateAlarmsExplained()
                }

                !timersRepository.fullScreenAlarmsAllowed && !timerPromptsRepository.wasLockScreenAlarmsExplained() -> {
                    sendEvent { TimersEvent.ExplainLockScreenAlarms }
                    timerPromptsRepository.markLockScreenAlarmsExplained()
                }
            }
        }
    }

    private companion object {
        val listOrder =
            compareBy<RunningTimer>(
                { !it.ringing },
                { it.isPaused },
                { it.endsAtMillis ?: Long.MAX_VALUE },
                { it.pausedRemainingMillis ?: 0L },
            )
    }
}
