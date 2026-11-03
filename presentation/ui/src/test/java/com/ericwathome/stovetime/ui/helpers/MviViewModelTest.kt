package com.ericwathome.stovetime.ui.helpers

import app.cash.turbine.test
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

private sealed interface CounterAction {
    data object OnClickAdd : CounterAction

    data object OnClickDone : CounterAction
}

private sealed interface CounterEvent {
    data object Close : CounterEvent
}

private data class CounterState(
    val count: Int = 0,
    val loaded: Boolean = false,
)

private class CounterViewModel : MviViewModel<CounterAction, CounterState, CounterEvent>(CounterState()) {
    override fun onStateSubscription() {
        updateState { it.copy(loaded = true) }
    }

    override fun onAction(action: CounterAction) {
        when (action) {
            CounterAction.OnClickAdd -> {
                updateState { it.copy(count = currentState.count + 1) }
            }

            CounterAction.OnClickDone -> {
                sendEvent { CounterEvent.Close }
            }
        }
    }
}

class MviViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun subscribing_to_state_runs_the_subscription_hook() =
        runTest {
            // given
            val sut = CounterViewModel()

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().loaded).isTrue()
            }
        }

    @Test
    fun an_action_changes_state() =
        runTest {
            // given
            val sut = CounterViewModel()

            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(CounterAction.OnClickAdd)

                // then
                assertThat(expectMostRecentItem().count).isEqualTo(1)
            }
        }

    @Test
    fun an_action_can_send_a_one_off_event() =
        runTest {
            // given
            val sut = CounterViewModel()

            sut.event.test {
                // when
                sut.onAction(CounterAction.OnClickDone)

                // then
                assertThat(awaitItem()).isEqualTo(CounterEvent.Close)
            }
        }
}
