package com.ericwathome.stovetime

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.timers.alarm.AlarmRoute
import com.ericwathome.stovetime.sdk.timer.StovetimeTimers

class AlarmActivity : ComponentActivity() {
    private var timerId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        timerId = intent.timerId()
        if (timerId == null) {
            finish()
            return
        }
        setContent {
            StovetimeTheme {
                timerId?.let { id -> AlarmRoute(timerId = id, onDismiss = ::finish) }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.timerId()?.let { timerId = it }
    }

    private fun Intent.timerId(): String? = getStringExtra(StovetimeTimers.EXTRA_TIMER_ID)
}
