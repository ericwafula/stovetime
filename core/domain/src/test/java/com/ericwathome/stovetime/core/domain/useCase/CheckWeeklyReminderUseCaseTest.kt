package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.core.data.repo.reminder.ReminderRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Before
import org.junit.Test

class CheckWeeklyReminderUseCaseTest {
    @MockK
    private lateinit var observeProgress: ObserveProgressUseCase

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private lateinit var reminderRepository: ReminderRepository

    private val saturdayEvening = LocalDateTime(2026, 11, 7, 18, 30)
    private lateinit var sut: CheckWeeklyReminderUseCaseImpl

    private fun progress(
        cooks: Int,
        weekStreak: Int,
    ) = Progress(
        totalXp = 0,
        weekStreak = weekStreak,
        bestWeekStreak = weekStreak,
        thisWeek = WeekProgress(days = emptyList(), cooks = cooks, goal = 4),
        history = emptyList(),
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { settingsRepository.observeCookSettings() } returns flowOf(CookSettings())
        every { observeProgress() } returns flowOf(DataResult.Success(progress(cooks = 2, weekStreak = 5)))
        coEvery { reminderRepository.getLastReminderWeek() } returns DataResult.Success(null)
        sut = CheckWeeklyReminderUseCaseImpl(observeProgress, settingsRepository, reminderRepository)
    }

    @Test
    fun the_week_and_streak_come_from_the_progress() =
        runTest {
            // when
            val result = sut.check(saturdayEvening)

            // then
            assertThat(result).isEqualTo(DataResult.Success(WeeklyReminder(cooksLeft = 2, weekStreak = 5, weekStart = LocalDate(2026, 11, 2))))
        }

    @Test
    fun the_switch_off_skips_the_check() =
        runTest {
            // given
            every { settingsRepository.observeCookSettings() } returns flowOf(CookSettings(weeklyReminder = false))

            // when
            val result = sut.check(saturdayEvening)

            // then
            assertThat(result).isEqualTo(DataResult.Success(null))
            coVerify(exactly = 0) { reminderRepository.getLastReminderWeek() }
        }

    @Test
    fun the_week_already_reminded_is_skipped() =
        runTest {
            // given
            coEvery { reminderRepository.getLastReminderWeek() } returns DataResult.Success(LocalDate(2026, 11, 2))

            // then
            assertThat(sut.check(saturdayEvening)).isEqualTo(DataResult.Success(null))
        }

    @Test
    fun a_progress_error_passes_through() =
        runTest {
            // given
            val error = DataResult.Error(DataError.Unknown("disk"))
            every { observeProgress() } returns flowOf(error)

            // then
            assertThat(sut.check(saturdayEvening)).isEqualTo(error)
        }
}
