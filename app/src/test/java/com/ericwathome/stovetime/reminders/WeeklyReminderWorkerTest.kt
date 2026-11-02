package com.ericwathome.stovetime.reminders

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.repo.reminder.SaveLastReminderWeekRepository
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import com.ericwathome.stovetime.core.domain.useCase.CheckWeeklyReminderUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class WeeklyReminderWorkerTest {
    @MockK
    private lateinit var checkWeeklyReminder: CheckWeeklyReminderUseCase

    @MockK
    private lateinit var saveLastReminderWeek: SaveLastReminderWeekRepository

    @MockK
    private lateinit var notifier: WeeklyReminderNotifier

    private val reminder = WeeklyReminder(cooksLeft = 1, weekStreak = 3, weekStart = LocalDate(2026, 11, 2))

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { checkWeeklyReminder() } returns DataResult.Success(reminder)
        coEvery { saveLastReminderWeek(any()) } returns DataResult.Success(true)
        every { notifier.canNotify() } returns true
    }

    private suspend fun send() = sendWeeklyReminder(checkWeeklyReminder, saveLastReminderWeek, notifier)

    @Test
    fun a_due_reminder_is_posted_and_its_week_remembered() =
        runTest {
            // when
            val result = send()

            // then
            assertThat(result).isEqualTo(DataResult.Success(true))
            verify { notifier.notify(reminder) }
            coVerify { saveLastReminderWeek(LocalDate(2026, 11, 2)) }
        }

    @Test
    fun nothing_due_posts_nothing() =
        runTest {
            // given
            coEvery { checkWeeklyReminder() } returns DataResult.Success(null)

            // when
            send()

            // then
            verify(exactly = 0) { notifier.notify(any()) }
            coVerify(exactly = 0) { saveLastReminderWeek(any()) }
        }

    @Test
    fun without_the_notification_permission_nothing_is_posted_or_remembered() =
        runTest {
            // given
            every { notifier.canNotify() } returns false

            // when
            val result = send()

            // then
            assertThat(result).isEqualTo(DataResult.Success(false))
            verify(exactly = 0) { notifier.notify(any()) }
            coVerify(exactly = 0) { saveLastReminderWeek(any()) }
        }

    @Test
    fun a_week_that_fails_to_save_is_not_posted_so_a_retry_cannot_post_twice() =
        runTest {
            // given
            val error = DataResult.Error(DataError.Unknown("disk"))
            coEvery { saveLastReminderWeek(any()) } returns error

            // when
            val result = send()

            // then
            assertThat(result).isEqualTo(error)
            verify(exactly = 0) { notifier.notify(any()) }
        }

    @Test
    fun the_first_check_is_at_six_pm_today_when_it_is_still_earlier() {
        // given
        val zone = TimeZone.of("Africa/Nairobi")
        val now = LocalDateTime(2026, 11, 7, 15, 30).toInstant(zone)

        // then
        assertThat(untilNextReminderCheck(now, zone)).isEqualTo(2.hours + 30.minutes)
    }

    @Test
    fun the_first_check_is_at_six_pm_tomorrow_once_today_has_passed() {
        // given
        val zone = TimeZone.of("Africa/Nairobi")
        val now = LocalDateTime(2026, 11, 7, 18, 0).toInstant(zone)

        // then
        assertThat(untilNextReminderCheck(now, zone)).isEqualTo(24.hours)
    }
}
