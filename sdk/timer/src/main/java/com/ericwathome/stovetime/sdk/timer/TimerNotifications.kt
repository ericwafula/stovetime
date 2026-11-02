package com.ericwathome.stovetime.sdk.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import java.util.Date

internal class TimerNotifications(
    private val context: Context,
    private val config: TimerConfig,
) {
    private val manager = context.getSystemService(NotificationManager::class.java)

    fun createChannels() {
        val running =
            NotificationChannel(RUNNING_CHANNEL, context.getString(R.string.sdk_timer_channel_running_name), NotificationManager.IMPORTANCE_LOW)
                .apply {
                    description = context.getString(R.string.sdk_timer_channel_running_description)
                    setShowBadge(false)
                }
        val alarm =
            NotificationChannel(ALARM_CHANNEL, context.getString(R.string.sdk_timer_channel_alarm_name), NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    description = context.getString(R.string.sdk_timer_channel_alarm_description)
                    setSound(null, null)
                    enableVibration(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
        manager.createNotificationChannels(listOf(running, alarm))
    }

    fun showOngoing(timers: List<CookTimer>) {
        manager.notify(ONGOING_ID, ongoing(timers))
    }

    fun cancelOngoing() {
        manager.cancel(ONGOING_ID)
    }

    fun ongoing(timers: List<CookTimer>): Notification {
        val now = System.currentTimeMillis()
        val soonest = timers.firstOrNull()
        val builder =
            NotificationCompat
                .Builder(context, RUNNING_CHANNEL)
                .setSmallIcon(config.smallIcon)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setContentIntent(TimerIntents.openApp(context, config, soonest))
        when {
            soonest == null -> builder.setContentTitle(context.getString(R.string.sdk_timer_title))
            timers.size == 1 ->
                builder
                    .setContentTitle(soonest.name)
                    .setContentText(status(soonest, now))
            else ->
                builder
                    .setContentTitle(context.resources.getQuantityString(R.plurals.sdk_timer_running_count, timers.size, timers.size))
                    .setContentText(line(soonest, now))
                    .setStyle(NotificationCompat.InboxStyle().also { style -> timers.forEach { style.addLine(line(it, now)) } })
        }
        val countdownTo = soonest?.takeUnless { it.ringing }?.endsAtMillis
        if (countdownTo == null) {
            builder.setShowWhen(false)
        } else {
            builder
                .setWhen(countdownTo)
                .setShowWhen(true)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
        }
        return builder.build()
    }

    fun showAlarm(
        timer: CookTimer,
        sounding: Boolean,
    ) {
        val builder =
            NotificationCompat
                .Builder(context, ALARM_CHANNEL)
                .setSmallIcon(config.smallIcon)
                .setContentTitle(context.getString(R.string.sdk_timer_times_up))
                .setContentText(timer.name)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(sounding)
                .setAutoCancel(false)
                .setOnlyAlertOnce(!sounding)
                .setWhen(timer.endsAtMillis ?: System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(TimerIntents.openApp(context, config, timer))
                .setDeleteIntent(TimerIntents.stop(context, timer.id))
                .addAction(0, context.getString(R.string.sdk_timer_action_stop), TimerIntents.stop(context, timer.id))
                .addAction(0, context.getString(R.string.sdk_timer_action_snooze), TimerIntents.snooze(context, timer.id))
        timer.detail?.let { detail ->
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.sdk_timer_line, timer.name, detail)))
        }
        if (sounding) {
            TimerIntents.fullScreen(context, config, timer.id)?.let { builder.setFullScreenIntent(it, true) }
        }
        manager.notify(alarmId(timer.id), builder.build())
    }

    fun cancelAlarm(id: String) {
        manager.cancel(alarmId(id))
    }

    private fun line(
        timer: CookTimer,
        nowMillis: Long,
    ) = context.getString(R.string.sdk_timer_line, timer.name, status(timer, nowMillis))

    private fun status(
        timer: CookTimer,
        nowMillis: Long,
    ): String =
        when {
            timer.ringing -> context.getString(R.string.sdk_timer_times_up)
            timer.isPaused ->
                context.getString(R.string.sdk_timer_paused, DateUtils.formatElapsedTime(timer.remainingMillis(nowMillis) / 1000))
            else -> context.getString(R.string.sdk_timer_ends_at, DateFormat.getTimeFormat(context).format(Date(timer.endsAtMillis!!)))
        }

    private fun alarmId(id: String): Int = id.hashCode().let { if (it == ONGOING_ID) it + 1 else it }

    companion object {
        const val ONGOING_ID = 0x5707
        private const val RUNNING_CHANNEL = "stovetime_timers_running"
        private const val ALARM_CHANNEL = "stovetime_timers_alarm"
    }
}
