package com.ericwathome.stovetime.sdk.timer

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class TimerEngineImpl(
    private val context: Context,
    config: TimerConfig,
) : TimerEngine {
    private val lock = Any()
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val alarms = AlarmScheduler(context)
    private val notifications = TimerNotifications(context, config)
    private val sound = AlarmSound(context)
    private val handler = Handler(Looper.getMainLooper())
    private val soundingUntil = mutableMapOf<String, Long>()
    private val silenceDueAlarms = Runnable { synchronized(lock) { refreshSound() } }

    private var book = TimerBook()
    private val _timers = MutableStateFlow<List<CookTimer>>(emptyList())

    override val timers: StateFlow<List<CookTimer>> = _timers.asStateFlow()

    override val exactAlarmsAllowed: Boolean get() = alarms.exactAllowed
    override val fullScreenAlarmsAllowed: Boolean
        get() =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()

    override fun start(
        name: String,
        durationMillis: Long,
        detail: String?,
        payload: String?,
    ): String {
        var id = ""
        update { now -> id = book.start(name, durationMillis, detail, payload, now).id }
        return id
    }

    override fun pause(id: String) = update { now -> book.pause(id, now) }

    override fun resume(id: String) = update { now -> book.resume(id, now) }

    override fun addMinute(id: String) = update { now -> book.addMinute(id, now) }

    override fun cancel(id: String) = update { book.cancel(id) }

    override fun stopRinging(id: String) = update { book.stopRinging(id) }

    override fun snooze(id: String) = update { now -> book.snooze(id, now) }

    fun restore() {
        synchronized(lock) {
            notifications.createChannels()
            book = TimerBook.fromJson(preferences.getString(TIMERS_KEY, null))
            val restored = book.timers
            restored.filter { it.ringing }.forEach { notifications.showAlarm(it, sounding = false) }
            book.ringDue(now())
            commit(before = restored, scheduledBefore = emptyList())
        }
    }

    fun onAlarm() = update { now -> book.ringDue(now) }

    fun onSystemRestart() {
        synchronized(lock) {
            val before = book.timers
            book.ringDue(now())
            commit(before = before, scheduledBefore = emptyList())
        }
    }

    fun ongoingNotification(): Notification = synchronized(lock) { notifications.ongoing(book.timers) }

    private fun update(change: (nowMillis: Long) -> Unit) {
        synchronized(lock) {
            val before = book.timers
            change(now())
            commit(before = before, scheduledBefore = before)
        }
    }

    private fun commit(
        before: List<CookTimer>,
        scheduledBefore: List<CookTimer>,
    ) {
        val after = book.timers
        preferences.edit(commit = true) { putString(TIMERS_KEY, book.toJson()) }
        alarms.sync(scheduledBefore, after)
        _timers.value = after

        val ringingBefore = before.ringingIds()
        val ringingAfter = after.ringingIds()
        (ringingBefore - ringingAfter).forEach(notifications::cancelAlarm)
        after.filter { it.id in ringingAfter - ringingBefore }.forEach { timer ->
            soundingUntil[timer.id] = now() + TimerBook.ONE_MINUTE
            notifications.showAlarm(timer, sounding = true)
        }
        soundingUntil.keys.retainAll(ringingAfter)
        refreshSound()
        keepAlive(after)
    }

    private fun refreshSound() {
        val now = now()
        val silenced = soundingUntil.filterValues { it <= now }.keys
        silenced.forEach(soundingUntil::remove)
        book.timers.filter { it.id in silenced }.forEach { notifications.showAlarm(it, sounding = false) }

        handler.removeCallbacks(silenceDueAlarms)
        val nextSilence = soundingUntil.values.minOrNull()
        if (nextSilence == null) {
            sound.stop()
        } else {
            sound.start()
            handler.postDelayed(silenceDueAlarms, nextSilence - now)
        }
    }

    private fun keepAlive(timers: List<CookTimer>) {
        if (timers.isEmpty()) {
            TimerService.running?.stop()
            notifications.cancelOngoing()
            return
        }
        notifications.showOngoing(timers)
        if (TimerService.running != null) return
        try {
            ContextCompat.startForegroundService(context, Intent(context, TimerService::class.java))
        } catch (_: IllegalStateException) {
            // Not allowed from the background; the alarm, boot and notification-action paths start it instead.
        } catch (_: SecurityException) {
        }
    }

    private fun List<CookTimer>.ringingIds() = filter { it.ringing }.map { it.id }.toSet()

    private fun now() = System.currentTimeMillis()

    private companion object {
        const val PREFERENCES = "stovetime_sdk_timer"
        const val TIMERS_KEY = "timers"
    }
}
