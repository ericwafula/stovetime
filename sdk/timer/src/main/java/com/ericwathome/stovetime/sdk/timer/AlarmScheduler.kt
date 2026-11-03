package com.ericwathome.stovetime.sdk.timer

import android.app.AlarmManager
import android.content.Context
import android.os.Build

internal class AlarmScheduler(
    private val context: Context,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    val exactAllowed: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    fun sync(
        before: List<CookTimer>,
        after: List<CookTimer>,
    ) {
        val changes = alarmChanges(before, after)
        changes.cancel.forEach(::cancel)
        changes.schedule.forEach { (id, endsAt) -> schedule(id, endsAt) }
    }

    private fun schedule(
        id: String,
        endsAtMillis: Long,
    ) {
        val alarm = TimerIntents.alarm(context, id)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endsAtMillis, alarm)
                return
            } catch (_: SecurityException) {
            }
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endsAtMillis, alarm)
    }

    private fun cancel(id: String) {
        alarmManager.cancel(TimerIntents.alarm(context, id))
    }
}
