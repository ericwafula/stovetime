package com.ericwathome.stovetime.core.data.mappers.timers

import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.sdk.timer.CookTimer

internal fun CookTimer.toData(): RunningTimer =
    RunningTimer(
        id = id,
        name = name,
        detail = detail,
        durationMillis = durationMillis,
        endsAtMillis = endsAtMillis,
        pausedRemainingMillis = pausedRemainingMillis,
        target = TimerTarget.decode(payload),
        ringing = ringing,
    )
