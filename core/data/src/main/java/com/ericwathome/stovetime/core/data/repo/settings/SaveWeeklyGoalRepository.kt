package com.ericwathome.stovetime.core.data.repo.settings

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource

fun interface SaveWeeklyGoalRepository {
    suspend operator fun invoke(goal: Int): DataResult<Boolean>
}

internal class SaveWeeklyGoalRepositoryImpl(
    private val cookSettingsLocalDatasource: CookSettingsLocalDatasource,
) : SaveWeeklyGoalRepository {
    override suspend fun invoke(goal: Int): DataResult<Boolean> =
        cookSettingsLocalDatasource.weeklyGoal
            .save(goal.coerceIn(CookSettings.MIN_WEEKLY_GOAL, CookSettings.MAX_WEEKLY_GOAL))
            .mapToData { it }
}
