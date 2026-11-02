package com.ericwathome.stovetime.features.timers.alarm

import app.cash.turbine.test
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.testing.FakeTimersRepository
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlarmViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = 1_000_000L
    private lateinit var timersRepository: FakeTimersRepository
    private lateinit var sut: AlarmViewModel

    @Before
    fun setup() {
        timersRepository = FakeTimersRepository(nowMillis = { now })
        sut = AlarmViewModel(timersRepository)
    }

    private fun ringingTimer(name: String): String {
        val id = timersRepository.start(name = name, durationMillis = 60_000, detail = null, target = TimerTarget.Quick)
        timersRepository.ring(id)
        return id
    }

    @Test
    fun the_alarm_shows_the_ringing_timer_it_was_opened_for() =
        runTest {
            // given
            val eggs = ringingTimer("Jammy eggs · Step 2")

            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(AlarmAction.OnShowTimer(eggs))

                // then
                assertThat(awaitItem().timer?.name).isEqualTo("Jammy eggs · Step 2")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun stop_silences_the_alarm_and_clears_the_timer() =
        runTest {
            // given
            val eggs = ringingTimer("Jammy eggs · Step 2")
            sut.state.test {
                skipItems(1)
                sut.onAction(AlarmAction.OnShowTimer(eggs))
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }

            // when
            sut.onAction(AlarmAction.OnClickStop)

            // then
            assertThat(timersRepository.timers.value).isEmpty()
        }

    @Test
    fun stop_closes_the_alarm_when_no_other_timer_is_ringing() =
        runTest {
            // given
            val eggs = ringingTimer("Jammy eggs · Step 2")
            sut.state.test {
                skipItems(1)
                sut.onAction(AlarmAction.OnShowTimer(eggs))
                awaitItem()

                sut.event.test {
                    // when
                    sut.onAction(AlarmAction.OnClickStop)

                    // then
                    assertThat(awaitItem()).isEqualTo(AlarmEvent.Dismiss)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun plus_one_minute_silences_the_alarm_and_restarts_the_timer_for_a_minute() =
        runTest {
            // given
            val eggs = ringingTimer("Jammy eggs · Step 2")
            sut.state.test {
                skipItems(1)
                sut.onAction(AlarmAction.OnShowTimer(eggs))
                awaitItem()

                sut.event.test {
                    // when
                    sut.onAction(AlarmAction.OnClickPlusMinute)

                    // then
                    val timer = timersRepository.timers.value.single()
                    assertThat(timer.ringing).isFalse()
                    assertThat(timer.remainingMillis(now)).isEqualTo(60_000)
                    assertThat(awaitItem()).isEqualTo(AlarmEvent.Dismiss)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun two_timers_ending_together_each_get_their_own_alarm() =
        runTest {
            // given
            val eggs = ringingTimer("Jammy eggs · Step 2")
            val rice = ringingTimer("Rice")

            sut.state.test {
                skipItems(1)
                sut.onAction(AlarmAction.OnShowTimer(eggs))
                assertThat(awaitItem().timer?.id).isEqualTo(eggs)

                // when
                sut.onAction(AlarmAction.OnShowTimer(rice))

                // then
                assertThat(awaitItem().timer?.id).isEqualTo(rice)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun stopping_one_of_two_ringing_timers_shows_the_other_alarm() =
        runTest {
            // given
            val eggs = ringingTimer("Jammy eggs · Step 2")
            val rice = ringingTimer("Rice")

            sut.state.test {
                skipItems(1)
                sut.onAction(AlarmAction.OnShowTimer(rice))
                awaitItem()

                // when
                sut.onAction(AlarmAction.OnClickStop)

                // then
                assertThat(awaitItem().timer?.id).isEqualTo(eggs)
                assertThat(timersRepository.timers.value.map { it.id }).containsExactly(eggs)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun an_alarm_opened_for_a_timer_that_already_stopped_closes() =
        runTest {
            // given
            val rice = timersRepository.start(name = "Rice", durationMillis = 60_000, detail = null, target = TimerTarget.Quick)

            sut.state.test {
                skipItems(1)
                sut.event.test {
                    // when
                    sut.onAction(AlarmAction.OnShowTimer(rice))

                    // then
                    assertThat(awaitItem()).isEqualTo(AlarmEvent.Dismiss)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }
}
