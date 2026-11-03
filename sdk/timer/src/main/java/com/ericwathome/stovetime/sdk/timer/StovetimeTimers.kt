package com.ericwathome.stovetime.sdk.timer

import android.content.Context

object StovetimeTimers {
    const val EXTRA_TIMER_ID = "com.ericwathome.stovetime.sdk.timer.extra.TIMER_ID"

    @Volatile
    private var instance: TimerEngineImpl? = null

    fun initialize(
        context: Context,
        config: TimerConfig,
    ): TimerEngine =
        synchronized(this) {
            instance ?: TimerEngineImpl(context.applicationContext, config).also {
                instance = it
                it.restore()
            }
        }

    val engine: TimerEngine
        get() = checkNotNull(instance) { "Call StovetimeTimers.initialize from Application.onCreate first" }

    internal val engineOrNull: TimerEngineImpl? get() = instance
}
