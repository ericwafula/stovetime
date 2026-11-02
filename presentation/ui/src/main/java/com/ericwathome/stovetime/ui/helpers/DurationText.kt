package com.ericwathome.stovetime.ui.helpers

import com.ericwathome.stovetime.core.common.helpers.timerText

fun countdownText(remainingMillis: Long): String = timerText(remainingMillis)
