package com.ericwathome.stovetime.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.ericwathome.stovetime.core.common.helpers.timerText
import com.ericwathome.stovetime.ui.R

private const val MILLIS_PER_MINUTE = 60_000L
private const val MINUTES_PER_HOUR = 60L

fun countdownText(remainingMillis: Long): String = timerText(remainingMillis)

@Composable
@ReadOnlyComposable
fun cookTimeText(millis: Long): String {
    val minutes = (millis / MILLIS_PER_MINUTE).coerceAtLeast(1)
    val hours = minutes / MINUTES_PER_HOUR
    return if (hours > 0) {
        stringResource(R.string.cook_time_hours, hours, (minutes % MINUTES_PER_HOUR).toString().padStart(2, '0'))
    } else {
        stringResource(R.string.cook_time_minutes, minutes)
    }
}
