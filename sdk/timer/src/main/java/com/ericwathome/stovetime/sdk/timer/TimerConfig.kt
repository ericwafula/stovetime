package com.ericwathome.stovetime.sdk.timer

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes

data class TimerConfig(
    @param:DrawableRes val smallIcon: Int,
    val openAppIntent: (payload: String?) -> Intent,
    val alarmActivity: Class<out Activity>?,
)
