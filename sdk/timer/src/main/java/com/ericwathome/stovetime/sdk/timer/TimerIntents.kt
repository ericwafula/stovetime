package com.ericwathome.stovetime.sdk.timer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

internal object TimerIntents {
    const val ACTION_ALARM = "com.ericwathome.stovetime.sdk.timer.action.ALARM"
    const val ACTION_STOP = "com.ericwathome.stovetime.sdk.timer.action.STOP"
    const val ACTION_SNOOZE = "com.ericwathome.stovetime.sdk.timer.action.SNOOZE"

    private const val FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    fun alarm(
        context: Context,
        id: String,
    ): PendingIntent = broadcast(context, ACTION_ALARM, id)

    fun stop(
        context: Context,
        id: String,
    ): PendingIntent = broadcast(context, ACTION_STOP, id)

    fun snooze(
        context: Context,
        id: String,
    ): PendingIntent = broadcast(context, ACTION_SNOOZE, id)

    fun openApp(
        context: Context,
        config: TimerConfig,
        timer: CookTimer?,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            timer?.id.hashCode(),
            config.openAppIntent(timer?.payload),
            FLAGS,
        )

    fun fullScreen(
        context: Context,
        config: TimerConfig,
        id: String,
    ): PendingIntent? =
        config.alarmActivity?.let { activity ->
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, activity)
                    .setData(uriFor("alarm-screen", id))
                    .putExtra(StovetimeTimers.EXTRA_TIMER_ID, id)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION),
                FLAGS,
            )
        }

    private fun broadcast(
        context: Context,
        action: String,
        id: String,
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, TimerActionReceiver::class.java)
                .setAction(action)
                .setData(uriFor(action.substringAfterLast('.').lowercase(), id))
                .putExtra(StovetimeTimers.EXTRA_TIMER_ID, id),
            FLAGS,
        )

    private fun uriFor(
        kind: String,
        id: String,
    ) = "stovetime-timer://$kind/$id".toUri()
}
