package com.ericwathome.stovetime.reminders

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.DeviceTimeZone
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.repo.reminder.ReminderRepository
import com.ericwathome.stovetime.core.data.repo.reminder.SaveLastReminderWeekRepository
import com.ericwathome.stovetime.core.domain.useCase.CheckWeeklyReminderUseCase
import com.ericwathome.stovetime.core.domain.useCase.ReminderUseCases
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.Instant

const val WEEKLY_REMINDER_WORK_NAME = "WeeklyReminderWorker"

private const val MAX_REMINDER_ATTEMPTS = 3
private val REMINDER_CHECK_AT = LocalTime(hour = 18, minute = 0)

fun Context.scheduleWeeklyReminderWork() {
    val request =
        PeriodicWorkRequestBuilder<WeeklyReminderWorker>(Duration.ofDays(1))
            .setInitialDelay(untilNextReminderCheck(Now, DeviceTimeZone).inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .addTag(WEEKLY_REMINDER_WORK_NAME)
            .build()

    WorkManager
        .getInstance(this)
        .enqueueUniquePeriodicWork(WEEKLY_REMINDER_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
}

internal fun untilNextReminderCheck(
    now: Instant,
    timeZone: TimeZone,
): kotlin.time.Duration {
    val today = now.toLocalDateTime(timeZone).date
    val todayCheck = today.atTime(REMINDER_CHECK_AT).toInstant(timeZone)
    val nextCheck = if (todayCheck > now) todayCheck else today.plus(1, DateTimeUnit.DAY).atTime(REMINDER_CHECK_AT).toInstant(timeZone)
    return nextCheck - now
}

class WeeklyReminderWorker(
    context: Context,
    params: WorkerParameters,
    private val reminderUseCases: ReminderUseCases,
    private val reminderRepository: ReminderRepository,
    private val notifier: WeeklyReminderNotifier,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        when (sendWeeklyReminder(reminderUseCases.checkWeeklyReminder, reminderRepository.saveLastReminderWeek, notifier)) {
            is DataResult.Success -> Result.success()
            is DataResult.Error -> if (runAttemptCount + 1 >= MAX_REMINDER_ATTEMPTS) Result.failure() else Result.retry()
        }
}

internal suspend fun sendWeeklyReminder(
    checkWeeklyReminder: CheckWeeklyReminderUseCase,
    saveLastReminderWeek: SaveLastReminderWeekRepository,
    notifier: WeeklyReminderNotifier,
): DataResult<Boolean> {
    val reminder =
        when (val result = checkWeeklyReminder()) {
            is DataResult.Success -> result.data ?: return DataResult.Success(false)
            is DataResult.Error -> return result
        }
    if (!notifier.canNotify()) return DataResult.Success(false)
    val saved = saveLastReminderWeek(reminder.weekStart)
    if (saved is DataResult.Success) notifier.notify(reminder)
    return saved
}
