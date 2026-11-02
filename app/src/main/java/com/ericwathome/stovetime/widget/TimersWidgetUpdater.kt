package com.ericwathome.stovetime.widget

import android.content.Context
import com.ericwathome.stovetime.core.common.helpers.ApplicationScope
import com.ericwathome.stovetime.sdk.timer.StovetimeTimers
import kotlinx.coroutines.launch

class TimersWidgetUpdater(
    private val context: Context,
    private val applicationScope: ApplicationScope,
) {
    fun start() {
        applicationScope.launch {
            StovetimeTimers.engine.timers.collect { TimersWidgetProvider.refresh(context) }
        }
    }
}
