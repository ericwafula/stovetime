package com.ericwathome.stovetime.sdk.timer

import kotlinx.coroutines.flow.StateFlow

interface TimerEngine {
    val timers: StateFlow<List<CookTimer>>
    val exactAlarmsAllowed: Boolean
    val fullScreenAlarmsAllowed: Boolean

    fun start(
        name: String,
        durationMillis: Long,
        detail: String? = null,
        payload: String? = null,
    ): String

    fun pause(id: String)

    fun resume(id: String)

    fun addMinute(id: String)

    fun cancel(id: String)

    fun stopRinging(id: String)

    fun snooze(id: String)
}
