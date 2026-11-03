package com.ericwathome.stovetime.sdk.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class TimerActionReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val engine = StovetimeTimers.engineOrNull ?: return
        val id = intent.getStringExtra(StovetimeTimers.EXTRA_TIMER_ID) ?: return
        when (intent.action) {
            TimerIntents.ACTION_ALARM -> engine.onAlarm()
            TimerIntents.ACTION_STOP -> engine.stopRinging(id)
            TimerIntents.ACTION_SNOOZE -> engine.snooze(id)
        }
    }
}
