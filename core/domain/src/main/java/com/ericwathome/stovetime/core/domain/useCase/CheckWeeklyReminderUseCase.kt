package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.DeviceTimeZone
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.repo.reminder.ReminderRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import com.ericwathome.stovetime.core.domain.rules.weeklyReminder
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

fun interface CheckWeeklyReminderUseCase {
    suspend operator fun invoke(): DataResult<WeeklyReminder?>
}

internal class CheckWeeklyReminderUseCaseImpl(
    private val observeProgress: ObserveProgressUseCase,
    private val settingsRepository: SettingsRepository,
    private val reminderRepository: ReminderRepository,
) : CheckWeeklyReminderUseCase {
    override suspend fun invoke(): DataResult<WeeklyReminder?> = check(now = Now.toLocalDateTime(DeviceTimeZone))

    internal suspend fun check(now: LocalDateTime): DataResult<WeeklyReminder?> {
        val settings = settingsRepository.observeCookSettings().first()
        if (!settings.weeklyReminder) return DataResult.Success(null)
        val progress =
            when (val result = observeProgress().first()) {
                is DataResult.Success -> result.data
                is DataResult.Error -> return result
            }
        val lastSentWeekStart =
            when (val result = reminderRepository.getLastReminderWeek()) {
                is DataResult.Success -> result.data
                is DataResult.Error -> return result
            }
        return DataResult.Success(
            weeklyReminder(
                now = now,
                thisWeek = progress.thisWeek,
                weekStreak = progress.weekStreak,
                enabled = settings.weeklyReminder,
                lastSentWeekStart = lastSentWeekStart,
            ),
        )
    }
}
