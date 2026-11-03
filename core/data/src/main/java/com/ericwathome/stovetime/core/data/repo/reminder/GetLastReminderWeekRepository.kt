package com.ericwathome.stovetime.core.data.repo.reminder

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.settings.WeeklyReminderLocalDatasource
import kotlinx.datetime.LocalDate

fun interface GetLastReminderWeekRepository {
    suspend operator fun invoke(): DataResult<LocalDate?>
}

internal class GetLastReminderWeekRepositoryImpl(
    private val weeklyReminderLocalDatasource: WeeklyReminderLocalDatasource,
) : GetLastReminderWeekRepository {
    override suspend fun invoke(): DataResult<LocalDate?> =
        weeklyReminderLocalDatasource.lastSentWeekStart.get().mapToData { epochDays ->
            epochDays?.let { LocalDate.fromEpochDays(it) }
        }
}
