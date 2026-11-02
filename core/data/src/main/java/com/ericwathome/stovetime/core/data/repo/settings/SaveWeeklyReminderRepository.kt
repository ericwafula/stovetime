package com.ericwathome.stovetime.core.data.repo.settings

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource

fun interface SaveWeeklyReminderRepository {
    suspend operator fun invoke(enabled: Boolean): DataResult<Boolean>
}

internal class SaveWeeklyReminderRepositoryImpl(
    private val cookSettingsLocalDatasource: CookSettingsLocalDatasource,
) : SaveWeeklyReminderRepository {
    override suspend fun invoke(enabled: Boolean): DataResult<Boolean> =
        cookSettingsLocalDatasource.weeklyReminder.save(enabled).mapToData { it }
}
