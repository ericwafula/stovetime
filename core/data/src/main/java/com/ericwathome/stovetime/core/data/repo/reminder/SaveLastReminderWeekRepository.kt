package com.ericwathome.stovetime.core.data.repo.reminder

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.settings.WeeklyReminderLocalDatasource
import kotlinx.datetime.LocalDate

fun interface SaveLastReminderWeekRepository {
    suspend operator fun invoke(weekStart: LocalDate): DataResult<Boolean>
}

internal class SaveLastReminderWeekRepositoryImpl(
    private val weeklyReminderLocalDatasource: WeeklyReminderLocalDatasource,
) : SaveLastReminderWeekRepository {
    override suspend fun invoke(weekStart: LocalDate): DataResult<Boolean> =
        weeklyReminderLocalDatasource.lastSentWeekStart.save(weekStart.toEpochDays()).mapToData { it }
}
