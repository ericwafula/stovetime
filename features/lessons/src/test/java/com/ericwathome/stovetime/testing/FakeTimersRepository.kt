package com.ericwathome.stovetime.testing

import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.core.data.repo.timers.TimersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeTimersRepository(
    private val nowMillis: () -> Long = { System.currentTimeMillis() },
) : TimersRepository {
    val timers = MutableStateFlow<List<RunningTimer>>(emptyList())
    override var exactAlarmsAllowed: Boolean = true
    private var nextId = 0

    override fun observeTimers(): Flow<List<RunningTimer>> = timers

    override fun start(
        name: String,
        durationMillis: Long,
        detail: String?,
        target: TimerTarget,
    ): String {
        val id = "timer-${nextId++}"
        timers.update {
            it +
                RunningTimer(
                    id = id,
                    name = name,
                    detail = detail,
                    durationMillis = durationMillis,
                    endsAtMillis = nowMillis() + durationMillis,
                    pausedRemainingMillis = null,
                    target = target,
                    ringing = false,
                )
        }
        return id
    }

    override fun pause(id: String) =
        change(id) { it.copy(endsAtMillis = null, pausedRemainingMillis = it.remainingMillis(nowMillis())) }

    override fun resume(id: String) =
        change(id) { it.copy(endsAtMillis = nowMillis() + (it.pausedRemainingMillis ?: 0), pausedRemainingMillis = null) }

    override fun addMinute(id: String) = change(id) { it.copy(endsAtMillis = it.endsAtMillis?.plus(60_000)) }

    override fun cancel(id: String) = timers.update { list -> list.filterNot { it.id == id } }

    override fun stopRinging(id: String) = cancel(id)

    override fun snooze(id: String) = change(id) { it.copy(endsAtMillis = nowMillis() + 60_000, ringing = false) }

    fun ring(id: String) = change(id) { it.copy(endsAtMillis = nowMillis(), ringing = true) }

    private fun change(
        id: String,
        transform: (RunningTimer) -> RunningTimer,
    ) = timers.update { list -> list.map { if (it.id == id) transform(it) else it } }
}
