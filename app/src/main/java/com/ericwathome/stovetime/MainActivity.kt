package com.ericwathome.stovetime

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ericwathome.stovetime.design.R as DesignR
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.navigation.StovetimeNavHost

class MainActivity : ComponentActivity() {
    private var timerPayload by mutableStateOf<String?>(null)
    private var openPath by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        applyWindowTheme(darkTheme = resources.configuration.isNightMode())
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            timerPayload = intent.timerPayload()
            openPath = intent.opensPath()
        }
        setContent {
            val darkTheme = isSystemInDarkTheme()
            DisposableEffect(darkTheme) {
                applyWindowTheme(darkTheme)
                onDispose {}
            }
            StovetimeTheme(darkTheme = darkTheme) {
                StovetimeNavHost(
                    timerPayload = timerPayload,
                    onTimerPayloadOpened = { timerPayload = null },
                    openPath = openPath,
                    onPathOpened = { openPath = false },
                )
            }
        }
    }

    private fun applyWindowTheme(darkTheme: Boolean) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
        )
        window.setBackgroundDrawableResource(DesignR.color.stovetime_canvas)
    }

    private fun Configuration.isNightMode(): Boolean = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

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
