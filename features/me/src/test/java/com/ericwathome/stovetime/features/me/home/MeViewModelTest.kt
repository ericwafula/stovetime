package com.ericwathome.stovetime.features.me.home

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.asLocalDate
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.dataOrNull
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var progressUseCases: ProgressUseCases

    private val progress = MutableStateFlow<DataResult<Progress>>(DataResult.Success(progressOf(emptyList())))
    private lateinit var sut: MeViewModel

    private fun cook(
        id: Long,
        kind: CookKind = CookKind.Lesson,
        dishId: String = "boiled-eggs",
        finishedAt: Long = 1_800_000_000_000L,
        xp: Int = 30,
    ) = Cook(
        id = id,
        dishId = dishId,
        kind = kind,
        name = dishId,
        photo = null,
        startedAt = finishedAt - 600_000,
        finishedAt = finishedAt,
        xp = xp,
        rating = null,
        weeklyGoal = 4,
    )

    private fun progressOf(
        history: List<Cook>,
        weekStreak: Int = 0,
        bestWeekStreak: Int = 0,
    ) = Progress(
        totalXp = history.sumOf { it.xp },
        weekStreak = weekStreak,
        bestWeekStreak = bestWeekStreak,
        thisWeek = WeekProgress(days = emptyList(), cooks = 0, goal = 4),
        history = history,
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { progressUseCases.observeProgress() } returns progress
        sut = MeViewModel(progressUseCases)
    }

    @Test
    fun a_fresh_install_shows_zero_totals_and_an_empty_history() =
        runTest {
            sut.state.test {
                // then
                val state = expectMostRecentItem()
                val shown = state.progress.dataOrNull()!!
                assertThat(shown.totalXp).isEqualTo(0)
                assertThat(shown.weekStreak).isEqualTo(0)
                assertThat(shown.bestWeekStreak).isEqualTo(0)
                assertThat(shown.cookCount).isEqualTo(0)
                assertThat(shown.history).isEmpty()
                assertThat(state.cookingSince).isNull()
            }
        }

    @Test
    fun several_cooks_show_their_totals() =
        runTest {
            sut.state.test {
                skipItems(1)

                // when
                progress.value = DataResult.Success(progressOf(listOf(cook(3, xp = 10), cook(2, xp = 30), cook(1, xp = 30)), weekStreak = 2, bestWeekStreak = 2))

                // then
                val shown = expectMostRecentItem().progress.dataOrNull()!!
                assertThat(shown.totalXp).isEqualTo(70)
                assertThat(shown.cookCount).isEqualTo(3)
                assertThat(shown.weekStreak).isEqualTo(2)
            }
        }

    @Test
    fun the_best_streak_stays_after_the_current_streak_resets() =
        runTest {
            sut.state.test {
                skipItems(1)

                // when
                progress.value = DataResult.Success(progressOf(listOf(cook(1)), weekStreak = 0, bestWeekStreak = 3))

                // then
                val shown = expectMostRecentItem().progress.dataOrNull()!!
                assertThat(shown.weekStreak).isEqualTo(0)
                assertThat(shown.bestWeekStreak).isEqualTo(3)
            }
        }

    @Test
    fun the_history_keeps_the_newest_cook_first_and_dates_the_first_cook() =
        runTest {
            sut.state.test {
                skipItems(1)
                val oldest = cook(1, finishedAt = 1_700_000_000_000L)

                // when
                progress.value = DataResult.Success(progressOf(listOf(cook(3), cook(2), oldest)))

                // then
                val state = expectMostRecentItem()
                assertThat(state.progress.dataOrNull()!!.history.map { it.id }).containsExactly(3L, 2L, 1L).inOrder()
                assertThat(state.cookingSince).isEqualTo(oldest.finishedAt.asLocalDate)
            }
        }

    @Test
    fun a_failed_load_shows_an_error_and_retry_loads_again() =
        runTest {
            progress.value = DataResult.Error(DataError.Unknown("broken"))
            sut.state.test {
                assertThat(expectMostRecentItem().progress).isInstanceOf(ViewState.Error::class.java)

                // when
                progress.value = DataResult.Success(progressOf(emptyList()))
                sut.onAction(MeAction.OnClickRetry)

                // then
                assertThat(expectMostRecentItem().progress).isInstanceOf(ViewState.Success::class.java)
            }
        }

    @Test
    fun tapping_a_lesson_cook_opens_its_intro() =
        runTest {
            progress.value = DataResult.Success(progressOf(listOf(cook(1, dishId = "jammy-eggs-on-toast"))))
            sut.state.test {
                skipItems(1)
                sut.event.test {
                    // when
                    sut.onAction(MeAction.OnClickCook(1))

                    // then
                    assertThat(awaitItem()).isEqualTo(MeEvent.OpenLesson("jammy-eggs-on-toast"))
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun tapping_a_recipe_cook_opens_its_recipe_page() =
        runTest {
            progress.value = DataResult.Success(progressOf(listOf(cook(1, kind = CookKind.Recipe, dishId = "52772"))))
            sut.state.test {
                skipItems(1)
                sut.event.test {
                    // when
                    sut.onAction(MeAction.OnClickCook(1))

                    // then
                    assertThat(awaitItem()).isEqualTo(MeEvent.OpenRecipe("52772"))
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun the_empty_history_points_back_to_the_path() =
        runTest {
            sut.event.test {
                // when
                sut.onAction(MeAction.OnClickGoToPath)

                // then
                assertThat(awaitItem()).isEqualTo(MeEvent.OpenPath)
            }
        }
}
