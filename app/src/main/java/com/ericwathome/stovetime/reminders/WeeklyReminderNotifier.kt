package com.ericwathome.stovetime.reminders

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ericwathome.stovetime.MainActivity
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import com.ericwathome.stovetime.ui.helpers.hasNotificationPermission

interface WeeklyReminderNotifier {
    fun canNotify(): Boolean

    fun notify(reminder: WeeklyReminder)
}

internal class WeeklyReminderNotifierImpl(
    private val context: Context,
) : WeeklyReminderNotifier {
    private val manager = context.getSystemService(NotificationManager::class.java)

    override fun canNotify(): Boolean = context.hasNotificationPermission() && manager.areNotificationsEnabled()

    override fun notify(reminder: WeeklyReminder) {
        if (!canNotify()) return
        createChannel()
        manager.notify(NOTIFICATION_ID, notification(reminder))
    }

    private fun createChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, context.getString(R.string.reminder_channel_name), NotificationManager.IMPORTANCE_LOW)
                .apply {
                    description = context.getString(R.string.reminder_channel_description)
                    setShowBadge(false)
                }
        manager.createNotificationChannel(channel)
    }

    private fun notification(reminder: WeeklyReminder): Notification {
        val text = weeklyReminderText(reminder)
        val openPath =
            PendingIntent.getActivity(
                context,
                REQUEST_OPEN_PATH,
                MainActivity.pathIntent(context),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_reminder)
            .setContentTitle(context.getString(text.resId, *text.formatArgs.toTypedArray()))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openPath)
            .setAutoCancel(true)
            .build()
    }

    private companion object {
        const val CHANNEL_ID = "weekly_reminder"
        const val NOTIFICATION_ID = 5_300
        const val REQUEST_OPEN_PATH = 5_301
    }
}
