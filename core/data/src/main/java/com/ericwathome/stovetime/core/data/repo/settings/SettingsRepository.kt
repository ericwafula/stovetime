package com.ericwathome.stovetime.core.data.repo.settings

interface SettingsRepository {
    val observeCookSettings: ObserveCookSettingsRepository
    val saveWeeklyGoal: SaveWeeklyGoalRepository
    val saveKeepScreenOn: SaveKeepScreenOnRepository
}

internal class SettingsRepositoryImpl(
    override val observeCookSettings: ObserveCookSettingsRepository,
    override val saveWeeklyGoal: SaveWeeklyGoalRepository,
    override val saveKeepScreenOn: SaveKeepScreenOnRepository,
) : SettingsRepository
