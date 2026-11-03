package com.ericwathome.stovetime.ui.helpers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<Action, State, Event>(
    initialState: State,
) : ViewModel() {
    private val _event = Channel<Event>()
    val event = _event.receiveAsFlow()

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> =
        _state
            .onStart { onStateSubscription() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialState)

    protected val currentState: State
        get() = _state.value

    protected open fun onStateSubscription() {}

    open fun onAction(action: Action) {}

    protected fun updateState(block: (State) -> State) {
        _state.update(block)
    }

    protected fun sendEvent(block: () -> Event) {
        viewModelScope.launch { _event.send(block()) }
    }
}
