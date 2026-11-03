package com.ericwathome.stovetime.features.timers.alarm

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.repo.timers.TimersRepository
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import kotlinx.coroutines.launch

sealed interface AlarmAction {
    data class OnShowTimer(
        val timerId: String,
    ) : AlarmAction

    data object OnClickStop : AlarmAction

    data object OnClickPlusMinute : AlarmAction
}

sealed interface AlarmEvent {
    data object Dismiss : AlarmEvent
}

data class AlarmState(
    val timer: RunningTimer? = null,
)

class AlarmViewModel(
    private val timersRepository: TimersRepository,
) : MviViewModel<AlarmAction, AlarmState, AlarmEvent>(AlarmState()) {
    private var started = false
    private var dismissed = false
    private var shownId: String? = null
    private var latestTimers: List<RunningTimer>? = null

    override fun onStateSubscription() {
        if (started) return
        started = true
        viewModelScope.launch {
            timersRepository.observeTimers().collect { timers ->
                latestTimers = timers
                showRingingTimer()
            }
        }
    }

    override fun onAction(action: AlarmAction) {
        when (action) {
            is AlarmAction.OnShowTimer -> {
                onShowTimer(action.timerId)
            }

            AlarmAction.OnClickStop -> {
                currentState.timer?.let { timersRepository.stopRinging(it.id) }
            }

            AlarmAction.OnClickPlusMinute -> {
                currentState.timer?.let { timersRepository.snooze(it.id) }
            }
        }
    }

    private fun onShowTimer(timerId: String) {
        shownId = timerId
        dismissed = false
        showRingingTimer()
    }

    private fun showRingingTimer() {
        val timers = latestTimers ?: return
        val requested = shownId ?: return
        val shown = timers.firstOrNull { it.id == requested && it.ringing } ?: timers.firstOrNull { it.ringing }
        if (shown == null) {
            if (!dismissed) {
                dismissed = true
                sendEvent { AlarmEvent.Dismiss }
            }
            return
        }
        shownId = shown.id
        updateState { it.copy(timer = shown) }
    }
}
