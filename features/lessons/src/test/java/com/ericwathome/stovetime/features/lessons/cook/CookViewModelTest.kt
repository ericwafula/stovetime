package com.ericwathome.stovetime.features.lessons.cook

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.cook.CookStep
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.core.data.repo.cook.CookRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.data.repo.settings.TimerPromptsRepository
import com.ericwathome.stovetime.testing.FakeTimersRepository
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CookViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var cookRepository: CookRepository

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private lateinit var timerPromptsRepository: TimerPromptsRepository

    private val timersRepository = FakeTimersRepository()
    private val args = CookArgs(kind = CookKind.Lesson, dishId = "jammy-eggs-on-toast", startedAt = 1_000, step = 0)
    private lateinit var sut: CookViewModel

    private val plan =
        CookPlan(
            kind = CookKind.Lesson,
            dishId = "jammy-eggs-on-toast",
            name = "Jammy eggs on toast",
            photo = null,
            baseXp = 30,
            steps =
                listOf(
                    CookStep("Boil water", null, null, null, null),
                    CookStep("Lower the eggs", null, 390, "for a jammy yolk", "Go slowly"),
                    CookStep("Toast", null, null, null, null),
                ),
        )

    private fun createViewModel(step: Int = 0) =
        CookViewModel(
            args = args.copy(step = step),
            savedStateHandle = SavedStateHandle(),
            cookRepository = cookRepository,
            settingsRepository = settingsRepository,
            timersRepository = timersRepository,
            timerPromptsRepository = timerPromptsRepository,
        )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { cookRepository.getCookPlan(any(), any()) } returns DataResult.Success(plan)
        every { settingsRepository.observeCookSettings() } returns flowOf(CookSettings())
        coEvery { timerPromptsRepository.wasNotificationsExplained() } returns true
        sut = createViewModel()
    }

    @Test
    fun the_progress_matches_the_step_you_are_on() =
        runTest {
            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(CookAction.OnClickNext)

                // then
                val state = expectMostRecentItem()
                assertThat(state.step).isEqualTo(1)
                assertThat(state.progress).isWithin(0.001f).of(2f / 3f)
            }
        }

    @Test
    fun back_goes_to_the_previous_step() =
        runTest {
            sut.state.test {
                skipItems(1)
                sut.onAction(CookAction.OnClickNext)

                // when
                sut.onAction(CookAction.OnClickBack)

                // then
                assertThat(expectMostRecentItem().step).isEqualTo(0)
            }
        }

    @Test
    fun back_from_step_one_asks_to_leave() =
        runTest {
            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(CookAction.OnClickBack)

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.AskToLeave)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun closing_asks_before_leaving() =
        runTest {
            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(CookAction.OnClickClose)

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.AskToLeave)

                    // when
                    sut.onAction(CookAction.OnConfirmLeave(keepTimers = null))

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.Close)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun the_last_step_finishes_the_cook() =
        runTest {
            // given
            sut = createViewModel(step = 2)

            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(CookAction.OnClickNext)

                    // then
                    val event = awaitItem() as CookEvent.Finish
                    assertThat(event.dishId).isEqualTo("jammy-eggs-on-toast")
                    assertThat(event.startedAt).isEqualTo(1_000)
                    assertThat(event.finishedAt).isAtLeast(1_000)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun a_started_timer_counts_down_on_the_card_and_is_named_after_the_step() =
        runTest {
            // given
            sut = createViewModel(step = 1)

            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(CookAction.OnClickStartTimer(name = "Jammy eggs on toast • Step 2", notificationsAllowed = true))

                // then
                val timer = expectMostRecentItem().currentTimer!!
                assertThat(timer.name).isEqualTo("Jammy eggs on toast • Step 2")
                assertThat(timer.durationMillis).isEqualTo(390_000)
                assertThat(timer.target).isEqualTo(TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", 1_000, 1))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun a_timer_that_ends_shows_time_is_up() =
        runTest {
            // given
            sut = createViewModel(step = 1)

            sut.state.test {
                skipItems(1)
                sut.onAction(CookAction.OnClickStartTimer(name = "Step 2", notificationsAllowed = true))
                val id = expectMostRecentItem().currentTimer!!.id

                // when
                timersRepository.ring(id)

                // then
                assertThat(expectMostRecentItem().currentTimer?.ringing).isTrue()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun moving_on_keeps_the_timer_running_as_a_chip() =
        runTest {
            // given
            sut = createViewModel(step = 1)

            sut.state.test {
                skipItems(1)
                sut.onAction(CookAction.OnClickStartTimer(name = "Step 2", notificationsAllowed = true))

                // when
                sut.onAction(CookAction.OnClickNext)

                // then
                val state = expectMostRecentItem()
                assertThat(state.currentTimer).isNull()
                assertThat(state.otherTimers).hasSize(1)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun coming_back_to_cook_mode_shows_the_timers_real_time_left() =
        runTest {
            // given
            timersRepository.start("Step 2", 390_000, null, TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", 1_000, 1))
            sut = createViewModel(step = 1)

            sut.state.test {
                // then
                val state = expectMostRecentItem()
                assertThat(state.currentTimer).isNotNull()
                assertThat(state.currentTimer!!.remainingMillis(state.nowMillis)).isAtMost(390_000)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun leaving_with_a_timer_running_asks_whether_to_keep_it() =
        runTest {
            // given
            sut = createViewModel(step = 1)

            sut.state.test {
                skipItems(1)
                sut.onAction(CookAction.OnClickStartTimer(name = "Step 2", notificationsAllowed = true))

                sut.event.test {
                    // when
                    sut.onAction(CookAction.OnConfirmLeave(keepTimers = null))

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.AskToKeepTimersOnLeave)

                    // when
                    sut.onAction(CookAction.OnConfirmLeave(keepTimers = false))

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.Close)
                    assertThat(timersRepository.timers.value).isEmpty()
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun finishing_with_a_timer_running_asks_whether_to_keep_it() =
        runTest {
            // given
            sut = createViewModel(step = 1)

            sut.state.test {
                skipItems(1)
                sut.onAction(CookAction.OnClickStartTimer(name = "Step 2", notificationsAllowed = true))
                sut.onAction(CookAction.OnClickNext)

                sut.event.test {
                    // when
                    sut.onAction(CookAction.OnClickNext)

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.AskToKeepTimersOnFinish)

                    // when
                    sut.onAction(CookAction.OnConfirmFinish(keepTimers = true))

                    // then
                    assertThat(awaitItem()).isInstanceOf(CookEvent.Finish::class.java)
                    assertThat(timersRepository.timers.value).hasSize(1)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun the_first_timer_without_notifications_explains_them_first() =
        runTest {
            // given
            coEvery { timerPromptsRepository.wasNotificationsExplained() } returns false
            sut = createViewModel(step = 1)

            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(CookAction.OnClickStartTimer(name = "Step 2", notificationsAllowed = false))

                    // then
                    assertThat(awaitItem()).isEqualTo(CookEvent.ExplainNotifications)
                    assertThat(timersRepository.timers.value).isEmpty()

                    // when
                    sut.onAction(CookAction.OnNotificationPermissionResult)

                    // then
                    assertThat(timersRepository.timers.value).hasSize(1)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }
}
