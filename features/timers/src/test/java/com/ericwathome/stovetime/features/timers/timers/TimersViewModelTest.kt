package com.ericwathome.stovetime.features.timers.timers

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.core.data.repo.settings.TimerPromptsRepository
import com.ericwathome.stovetime.testing.FakeTimersRepository
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimersViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var timerPromptsRepository: TimerPromptsRepository

    private val now = 1_000_000L
    private val eggsStep = TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", startedAt = 42L, step = 1)
    private lateinit var timersRepository: FakeTimersRepository
    private lateinit var sut: TimersViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { timerPromptsRepository.wasNotificationsExplained() } returns true
        coEvery { timerPromptsRepository.markNotificationsExplained() } returns DataResult.Success(true)
        timersRepository = FakeTimersRepository(nowMillis = { now })
        sut = TimersViewModel(timersRepository, timerPromptsRepository)
    }

    private fun start(
        name: String,
        minutes: Int,
        target: TimerTarget = TimerTarget.Quick,
    ): String = timersRepository.start(name = name, durationMillis = minutes * 60_000L, detail = null, target = target)

    private fun startQuickTimer(name: String = "") {
        sut.onAction(TimersAction.OnClickNewQuickTimer)
        sut.onAction(TimersAction.OnQuickTimerNameChange(name))
        sut.onAction(TimersAction.OnClickStartQuickTimer(defaultNameFormat = "Timer %1\$d", notificationsAllowed = true))
    }

    @Test
    fun timers_are_listed_ringing_first_then_soonest_ending_then_paused() =
        runTest {
            // given
            val paused = start("Stock", minutes = 2)
            timersRepository.pause(paused)
            start("Rice", minutes = 12)
            start("Eggs", minutes = 6)
            val ringing = start("Onions", minutes = 20)
            timersRepository.ring(ringing)

            sut.state.test {
                // when
                val state = expectMostRecentItem()

                // then
                assertThat(state.timers.map { it.name }).containsExactly("Onions", "Eggs", "Rice", "Stock").inOrder()
            }
        }

    @Test
    fun pausing_from_the_list_pauses_the_timer() =
        runTest {
            // given
            val rice = start("Rice", minutes = 12)

            // when
            sut.onAction(TimersAction.OnClickPause(rice))

            // then
            assertThat(timersRepository.timers.value.single().isPaused).isTrue()
        }

    @Test
    fun resuming_from_the_list_starts_the_timer_again() =
        runTest {
            // given
            val rice = start("Rice", minutes = 12)
            timersRepository.pause(rice)

            // when
            sut.onAction(TimersAction.OnClickResume(rice))

            // then
            val timer = timersRepository.timers.value.single()
            assertThat(timer.isPaused).isFalse()
            assertThat(timer.remainingMillis(now)).isEqualTo(12 * 60_000L)
        }

    @Test
    fun plus_one_minute_from_the_list_adds_a_minute() =
        runTest {
            // given
            val rice = start("Rice", minutes = 12)

            // when
            sut.onAction(TimersAction.OnClickAddMinute(rice))

            // then
            assertThat(timersRepository.timers.value.single().remainingMillis(now)).isEqualTo(13 * 60_000L)
        }

    @Test
    fun cancelling_from_the_list_removes_the_timer() =
        runTest {
            // given
            val rice = start("Rice", minutes = 12)
            start("Eggs", minutes = 6)

            // when
            sut.onAction(TimersAction.OnClickCancel(rice))

            // then
            assertThat(timersRepository.timers.value.map { it.name }).containsExactly("Eggs")
        }

    @Test
    fun stopping_a_ringing_timer_from_the_list_clears_it() =
        runTest {
            // given
            val rice = start("Rice", minutes = 12)
            timersRepository.ring(rice)

            // when
            sut.onAction(TimersAction.OnClickStop(rice))

            // then
            assertThat(timersRepository.timers.value).isEmpty()
        }

    @Test
    fun tapping_a_step_timer_opens_its_cook_mode_at_that_step() =
        runTest {
            // given
            val eggs = start("Jammy eggs · Step 2", minutes = 6, target = eggsStep)

            sut.state.test {
                expectMostRecentItem()
                sut.event.test {
                    // when
                    sut.onAction(TimersAction.OnClickTimer(eggs))

                    // then
                    assertThat(awaitItem())
                        .isEqualTo(TimersEvent.OpenCookStep(kind = CookKind.Lesson, dishId = "jammy-eggs-on-toast", startedAt = 42L, step = 1))
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun tapping_a_quick_timer_opens_nothing() =
        runTest {
            // given
            val rice = start("Rice", minutes = 12)

            sut.state.test {
                expectMostRecentItem()
                sut.event.test {
                    // when
                    sut.onAction(TimersAction.OnClickTimer(rice))

                    // then
                    expectNoEvents()
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun new_quick_timer_opens_a_fresh_five_minute_form() =
        runTest {
            sut.event.test {
                // given
                sut.onAction(TimersAction.OnQuickTimerNameChange("Rice"))
                sut.onAction(TimersAction.OnQuickTimerAdjust(60))

                // when
                sut.onAction(TimersAction.OnClickNewQuickTimer)

                // then
                assertThat(awaitItem()).isEqualTo(TimersEvent.OpenQuickTimer)
                assertThat(sut.state.value.quickTimer.name).isEmpty()
                assertThat(sut.state.value.quickTimer.durationSeconds).isEqualTo(300)
            }
        }

    @Test
    fun the_quick_timer_length_changes_by_minutes_and_seconds_and_never_drops_below_ten_seconds() =
        runTest {
            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(TimersAction.OnQuickTimerAdjust(60))
                sut.onAction(TimersAction.OnQuickTimerAdjust(10))

                // then
                assertThat(expectMostRecentItem().quickTimer.durationSeconds).isEqualTo(370)

                // when
                repeat(8) { sut.onAction(TimersAction.OnQuickTimerAdjust(-60)) }

                // then
                assertThat(expectMostRecentItem().quickTimer.durationSeconds).isEqualTo(10)
            }
        }

    @Test
    fun a_quick_timer_with_a_name_starts_with_that_name_and_length() =
        runTest {
            // given
            sut.onAction(TimersAction.OnClickNewQuickTimer)
            sut.onAction(TimersAction.OnQuickTimerAdjust(-60))
            sut.onAction(TimersAction.OnQuickTimerAdjust(10))
            sut.onAction(TimersAction.OnQuickTimerNameChange("  Rice "))

            // when
            sut.onAction(TimersAction.OnClickStartQuickTimer(defaultNameFormat = "Timer %1\$d", notificationsAllowed = true))

            // then
            val timer = timersRepository.timers.value.single()
            assertThat(timer.name).isEqualTo("Rice")
            assertThat(timer.durationMillis).isEqualTo(250_000L)
            assertThat(timer.target).isEqualTo(TimerTarget.Quick)
            assertThat(timer.detail).isNull()
        }

    @Test
    fun quick_timers_without_a_name_are_called_timer_1_then_timer_2() =
        runTest {
            sut.state.test {
                // when
                startQuickTimer()
                startQuickTimer()

                // then
                assertThat(expectMostRecentItem().timers.map { it.name }).containsExactly("Timer 1", "Timer 2")
            }
        }

    @Test
    fun an_unnamed_quick_timer_takes_the_lowest_number_no_running_timer_uses() =
        runTest {
            // given
            start("Timer 1", minutes = 5)
            val second = start("Timer 2", minutes = 5)
            start("Timer 3", minutes = 5)
            timersRepository.cancel(second)

            sut.state.test {
                // when
                startQuickTimer()

                // then
                assertThat(expectMostRecentItem().timers.map { it.name }).contains("Timer 2")
            }
        }

    @Test
    fun the_first_timer_explains_notifications_before_asking_for_them() =
        runTest {
            // given
            coEvery { timerPromptsRepository.wasNotificationsExplained() } returns false
            sut.onAction(TimersAction.OnClickNewQuickTimer)

            sut.event.test {
                skipItems(1)

                // when
                sut.onAction(TimersAction.OnClickStartQuickTimer(defaultNameFormat = "Timer %1\$d", notificationsAllowed = false))

                // then
                assertThat(awaitItem()).isEqualTo(TimersEvent.ExplainNotifications)
                assertThat(timersRepository.timers.value).isEmpty()

                // when
                sut.onAction(TimersAction.OnNotificationsExplained)

                // then
                assertThat(awaitItem()).isEqualTo(TimersEvent.RequestNotificationPermission)

                // when
                sut.onAction(TimersAction.OnNotificationPermissionResult)

                // then
                assertThat(timersRepository.timers.value.single().name).isEqualTo("Timer 1")
            }
        }
}
