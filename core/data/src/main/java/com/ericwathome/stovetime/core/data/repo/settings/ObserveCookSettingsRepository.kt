package com.ericwathome.stovetime.core.data.repo.settings

import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun interface ObserveCookSettingsRepository {
    operator fun invoke(): Flow<CookSettings>
}

internal class ObserveCookSettingsRepositoryImpl(
    private val cookSettingsLocalDatasource: CookSettingsLocalDatasource,
) : ObserveCookSettingsRepository {
    override fun invoke(): Flow<CookSettings> =
        combine(
            cookSettingsLocalDatasource.weeklyGoal.observe(),
            cookSettingsLocalDatasource.keepScreenOn.observe(),
        ) { weeklyGoal, keepScreenOn ->
            val defaults = CookSettings()
            CookSettings(
                weeklyGoal = weeklyGoal?.coerceIn(CookSettings.MIN_WEEKLY_GOAL, CookSettings.MAX_WEEKLY_GOAL) ?: defaults.weeklyGoal,
                keepScreenOn = keepScreenOn ?: defaults.keepScreenOn,
            )
        }
}
