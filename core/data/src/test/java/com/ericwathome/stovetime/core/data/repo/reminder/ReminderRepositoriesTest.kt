package com.ericwathome.stovetime.core.data.repo.reminder

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.sources.settings.WeeklyReminderLocalDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Before
import org.junit.Test

class ReminderRepositoriesTest {
    @MockK
    private lateinit var weeklyReminderLocalDatasource: WeeklyReminderLocalDatasource

    private var stored: Long? = null

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { weeklyReminderLocalDatasource.lastSentWeekStart.get() } answers { LocalResult.Success(stored) }
        coEvery { weeklyReminderLocalDatasource.lastSentWeekStart.save(any()) } answers {
            stored = firstArg()
            LocalResult.Success(true)
        }
    }

    @Test
    fun no_reminder_sent_yet_reads_as_no_week() =
        runTest {
            // when
            val result = GetLastReminderWeekRepositoryImpl(weeklyReminderLocalDatasource)()

            // then
            assertThat(result).isEqualTo(DataResult.Success(null))
        }

    @Test
    fun the_saved_week_reads_back_as_the_same_date() =
        runTest {
            // given
            val weekStart = LocalDate(2026, 11, 2)

            // when
            SaveLastReminderWeekRepositoryImpl(weeklyReminderLocalDatasource)(weekStart)
            val result = GetLastReminderWeekRepositoryImpl(weeklyReminderLocalDatasource)()

            // then
            coVerify { weeklyReminderLocalDatasource.lastSentWeekStart.save(weekStart.toEpochDays()) }
            assertThat(result).isEqualTo(DataResult.Success(weekStart))
        }
}
