package com.ericwathome.stovetime.core.data.repo.reminder

interface ReminderRepository {
    val getLastReminderWeek: GetLastReminderWeekRepository
    val saveLastReminderWeek: SaveLastReminderWeekRepository
}

internal class ReminderRepositoryImpl(
    override val getLastReminderWeek: GetLastReminderWeekRepository,
    override val saveLastReminderWeek: SaveLastReminderWeekRepository,
) : ReminderRepository
