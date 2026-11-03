package com.ericwathome.stovetime.core.common.helpers

import kotlin.math.ceil

private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_HOUR = 3_600
private const val MILLIS_PER_SECOND = 1_000.0

fun timerText(remainingMillis: Long): String {
    val totalSeconds = ceil(remainingMillis.coerceAtLeast(0) / MILLIS_PER_SECOND).toInt()
    val hours = totalSeconds / SECONDS_PER_HOUR
    val minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    val paddedSeconds = seconds.toString().padStart(2, '0')
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:$paddedSeconds"
    } else {
        "$minutes:$paddedSeconds"
    }
}
