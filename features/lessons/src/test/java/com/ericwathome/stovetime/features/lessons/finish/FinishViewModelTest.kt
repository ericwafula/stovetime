package com.ericwathome.stovetime.features.lessons.finish

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.core.data.repo.cook.CookRepository
import com.ericwathome.stovetime.core.data.repo.progress.ProgressRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FinishViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var cookRepository: CookRepository

    @MockK
    private lateinit var progressRepository: ProgressRepository

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private lateinit var progressUseCases: ProgressUseCases

    private val args = FinishArgs(CookKind.Lesson, "jammy-eggs-on-toast", startedAt = 1_000, finishedAt = 841_000)
    private val savedStateHandle = SavedStateHandle()
    private val progress = MutableStateFlow<DataResult<Progress>>(DataResult.Success(progressWith(cooks = 2, history = emptyList())))

    private fun progressWith(
        cooks: Int,
        history: List<Cook>,
        streak: Int = 1,
    ) = Progress(
        totalXp = history.sumOf { it.xp },
        weekStreak = streak,
        bestWeekStreak = streak,
        thisWeek = WeekProgress(days = emptyList(), cooks = cooks, goal = 4),
        history = history,
    )

    private fun cook(xp: Int) =
        Cook(
            id = 1,
            dishId = "jammy-eggs-on-toast",
            kind = CookKind.Lesson,
            name = "Jammy eggs on toast",
            photo = null,
            startedAt = 0,
            finishedAt = 0,
            xp = xp,
            rating = null,
            weeklyGoal = 4,
        )

    private fun createViewModel() =
        FinishViewModel(args, savedStateHandle, cookRepository, progressRepository, settingsRepository, progressUseCases)

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { cookRepository.getCookPlan(any(), any()) } returns
            DataResult.Success(CookPlan(CookKind.Lesson, "jammy-eggs-on-toast", "Jammy eggs on toast", "lesson_jammy_eggs_on_toast", 30, emptyList()))
        every { progressUseCases.observeProgress() } returns progress
        every { progressUseCases.calculateCookXp(any(), any(), any()) } answers { if (thirdArg()) 10 else secondArg() }
        every { settingsRepository.observeCookSettings() } returns flowOf(CookSettings(weeklyGoal = 4))
        coEvery { progressRepository.saveCook(any()) } returns DataResult.Success(7L)
    }

    @Test
    fun the_cook_is_saved_once_with_its_time_and_xp() =
        runTest {
            // given
            val sut = createViewModel()

            sut.state.test {
                // then
                val dish = (expectMostRecentItem().dish as ViewState.Success).data
                assertThat(dish.xpEarned).isEqualTo(30)
                assertThat(dish.cookMillis).isEqualTo(840_000)
                cancelAndIgnoreRemainingEvents()
            }
            createViewModel().state.test { cancelAndIgnoreRemainingEvents() }

            coVerify(exactly = 1) {
                progressRepository.saveCook(
                    match { it.xp == 30 && it.startedAt == 1_000L && it.finishedAt == 841_000L && it.weeklyGoal == 4 && it.rating == null },
                )
            }
        }

    @Test
    fun cooking_a_lesson_again_earns_practice_xp() =
        runTest {
            // given
            progress.value = DataResult.Success(progressWith(cooks = 1, history = listOf(cook(xp = 30))))
            val sut = createViewModel()

            sut.state.test {
                // then
                assertThat((expectMostRecentItem().dish as ViewState.Success).data.xpEarned).isEqualTo(10)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun the_stats_and_week_row_follow_the_progress_rules() =
        runTest {
            // given
            val sut = createViewModel()

            sut.state.test {
                skipItems(1)

                // when
                progress.value = DataResult.Success(progressWith(cooks = 4, history = listOf(cook(xp = 30)), streak = 3))

                // then
                val state = expectMostRecentItem()
                assertThat(state.progress?.weekStreak).isEqualTo(3)
                assertThat(state.progress?.thisWeek?.cooks).isEqualTo(4)
                assertThat(state.goalJustMet).isTrue()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun choosing_a_rating_saves_it_and_it_can_be_changed() =
        runTest {
            // given
            val sut = createViewModel()

            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(FinishAction.OnSelectRating(Rating.PrettyGood))
                sut.onAction(FinishAction.OnSelectRating(Rating.NailedIt))

                // then
                assertThat(expectMostRecentItem().rating).isEqualTo(Rating.NailedIt)
                cancelAndIgnoreRemainingEvents()
            }
            coVerify { progressRepository.rateCook(7, Rating.PrettyGood) }
            coVerify { progressRepository.rateCook(7, Rating.NailedIt) }
        }

    @Test
    fun continue_goes_back_to_the_path() =
        runTest {
            // given
            val sut = createViewModel()

            sut.event.test {
                // when
                sut.onAction(FinishAction.OnClickContinue)

                // then
                assertThat(awaitItem()).isEqualTo(FinishEvent.Continue)
            }
        }
}
