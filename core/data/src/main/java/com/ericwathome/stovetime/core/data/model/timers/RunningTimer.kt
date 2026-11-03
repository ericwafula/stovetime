package com.ericwathome.stovetime.core.data.model.timers

data class RunningTimer(
    val id: String,
    val name: String,
    val detail: String?,
    val durationMillis: Long,
    val endsAtMillis: Long?,
    val pausedRemainingMillis: Long?,
    val target: TimerTarget,
    val ringing: Boolean,
) {
    val isPaused: Boolean
        get() = endsAtMillis == null && !ringing

    fun remainingMillis(nowMillis: Long): Long = (pausedRemainingMillis ?: ((endsAtMillis ?: nowMillis) - nowMillis)).coerceAtLeast(0)
}
