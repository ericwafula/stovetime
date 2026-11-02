package com.ericwathome.stovetime

import android.content.Context
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
import com.ericwathome.stovetime.navigation.StovetimeNavHost

class MainActivity : ComponentActivity() {
    private var timerPayload by mutableStateOf<String?>(null)
    private var openPath by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            timerPayload = intent.timerPayload()
            openPath = intent.opensPath()
        }
        setContent {
            StovetimeTheme {
                StovetimeNavHost(
                    timerPayload = timerPayload,
                    onTimerPayloadOpened = { timerPayload = null },
                    openPath = openPath,
                    onPathOpened = { openPath = false },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.timerPayload()?.let { timerPayload = it }
        if (intent.opensPath()) openPath = true
    }

    private fun Intent.timerPayload(): String? = getStringExtra(EXTRA_TIMER_PAYLOAD)

    private fun Intent.opensPath(): Boolean = getBooleanExtra(EXTRA_OPEN_PATH, false)

    companion object {
        private const val EXTRA_TIMER_PAYLOAD = "com.ericwathome.stovetime.extra.TIMER_PAYLOAD"
        private const val EXTRA_OPEN_PATH = "com.ericwathome.stovetime.extra.OPEN_PATH"

        fun timerIntent(
            context: Context,
            payload: String?,
        ): Intent =
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_TIMER_PAYLOAD, payload ?: "")

        fun pathIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_OPEN_PATH, true)
    }
}
