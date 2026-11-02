package com.ericwathome.stovetime.sdk.timer

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat

internal class TimerService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val engine = StovetimeTimers.engineOrNull
        if (engine == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceCompat.startForeground(
                    this,
                    TimerNotifications.ONGOING_ID,
                    engine.ongoingNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
                )
            } else {
                startForeground(TimerNotifications.ONGOING_ID, engine.ongoingNotification())
            }
        } catch (_: IllegalStateException) {
            stopSelf()
            return START_NOT_STICKY
        } catch (_: SecurityException) {
            stopSelf()
            return START_NOT_STICKY
        }
        running = this
        if (engine.timers.value.isEmpty()) stop()
        return START_STICKY
    }

    override fun onDestroy() {
        if (running === this) running = null
        super.onDestroy()
    }

    fun stop() {
        running = null
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        @Volatile
        var running: TimerService? = null
            private set
    }
}
