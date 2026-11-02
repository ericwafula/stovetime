package com.ericwathome.stovetime.reminders

import android.content.Context

fun interface ScheduleWeeklyReminderWork {
    operator fun invoke()
}

internal class ScheduleWeeklyReminderWorkImpl(
    private val context: Context,
) : ScheduleWeeklyReminderWork {
    override fun invoke() = context.scheduleWeeklyReminderWork()
}
