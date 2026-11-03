package com.ericwathome.stovetime.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun <T> CollectOneTimeEvent(
    events: Flow<T>,
    vararg keys: Any?,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(events, lifecycle, *keys) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect { currentOnEvent(it) }
            }
        }
    }
}
