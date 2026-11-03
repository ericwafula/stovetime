package com.ericwathome.stovetime.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val TICK_MILLIS = 250L

@Composable
fun rememberTickingNow(
    ticking: Boolean,
    initialMillis: Long,
): Long {
    var now by remember { mutableLongStateOf(initialMillis) }
    LaunchedEffect(ticking, initialMillis) {
        now = maxOf(initialMillis, System.currentTimeMillis())
        while (ticking && isActive) {
            delay(TICK_MILLIS)
            now = System.currentTimeMillis()
        }
    }
    return now
}
