package com.ericwathome.stovetime.sdk.timer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import java.io.IOException

internal class AlarmSound(
    private val context: Context,
) {
    private val vibrator = context.getSystemService(Vibrator::class.java)
    private var player: MediaPlayer? = null
    private var playing = false

    fun start() {
        if (playing) return
        playing = true
        player = loopingAlarm()
        vibrate()
    }

    fun stop() {
        if (!playing) return
        playing = false
        player?.release()
        player = null
        vibrator?.cancel()
    }

    private fun loopingAlarm(): MediaPlayer? =
        listOf(RingtoneManager.TYPE_ALARM, RingtoneManager.TYPE_RINGTONE, RingtoneManager.TYPE_NOTIFICATION)
            .mapNotNull { RingtoneManager.getDefaultUri(it) }
            .firstNotNullOfOrNull { uri ->
                val player = MediaPlayer()
                try {
                    player.setAudioAttributes(alarmAttributes)
                    player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
                    player.setDataSource(context, uri)
                    player.isLooping = true
                    player.prepare()
                    player.start()
                    player
                } catch (_: IOException) {
                    player.release()
                    null
                } catch (_: IllegalStateException) {
                    player.release()
                    null
                } catch (_: SecurityException) {
                    player.release()
                    null
                }
            }

    private fun vibrate() {
        val vibrator = vibrator?.takeIf { it.hasVibrator() } ?: return
        val pattern = VibrationEffect.createWaveform(longArrayOf(0, 800, 600), 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrator.vibrate(pattern, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, alarmAttributes)
        }
    }

    private companion object {
        val alarmAttributes: AudioAttributes =
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
    }
}
