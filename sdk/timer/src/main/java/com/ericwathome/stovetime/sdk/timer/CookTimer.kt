package com.ericwathome.stovetime.sdk.timer

data class CookTimer(
    val id: String,
    val name: String,
    val detail: String?,
    val durationMillis: Long,
    val endsAtMillis: Long?,
    val pausedRemainingMillis: Long?,
    val payload: String?,
    val ringing: Boolean,
) {
    init {
        require((endsAtMillis == null) != (pausedRemainingMillis == null)) {
            "A timer is either running with an end instant or paused with the time left"
        }
    }

    val isPaused: Boolean get() = endsAtMillis == null

    fun remainingMillis(nowMillis: Long): Long = (pausedRemainingMillis ?: (endsAtMillis!! - nowMillis)).coerceAtLeast(0)
}
