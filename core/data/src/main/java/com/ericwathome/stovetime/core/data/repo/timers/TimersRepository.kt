package com.ericwathome.stovetime.core.data.repo.timers

import com.ericwathome.stovetime.core.data.mappers.timers.toData
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.sdk.timer.TimerEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TimersRepository {
    val exactAlarmsAllowed: Boolean

    val fullScreenAlarmsAllowed: Boolean

    fun observeTimers(): Flow<List<RunningTimer>>

    fun start(
        name: String,
        durationMillis: Long,
        detail: String?,
        target: TimerTarget,
    ): String

    fun pause(id: String)

    fun resume(id: String)

    fun addMinute(id: String)

    fun cancel(id: String)

    fun stopRinging(id: String)

    fun snooze(id: String)
}

internal class TimersRepositoryImpl(
    private val engine: TimerEngine,
) : TimersRepository {
    override val exactAlarmsAllowed: Boolean
        get() = engine.exactAlarmsAllowed

    override val fullScreenAlarmsAllowed: Boolean
        get() = engine.fullScreenAlarmsAllowed

    override fun observeTimers(): Flow<List<RunningTimer>> = engine.timers.map { timers -> timers.map { it.toData() } }

    override fun start(
        name: String,
        durationMillis: Long,
        detail: String?,
        target: TimerTarget,
    ): String = engine.start(name = name, durationMillis = durationMillis, detail = detail, payload = target.encoded)

    override fun pause(id: String) = engine.pause(id)

    override fun resume(id: String) = engine.resume(id)

    override fun addMinute(id: String) = engine.addMinute(id)

    override fun cancel(id: String) = engine.cancel(id)

    override fun stopRinging(id: String) = engine.stopRinging(id)

    override fun snooze(id: String) = engine.snooze(id)
}
