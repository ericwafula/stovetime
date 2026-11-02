package com.ericwathome.stovetime.notifications

import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.navigation.Destination

fun TimerTarget.toDestination(): Destination =
    when (this) {
        is TimerTarget.CookStep -> Destination.Cook(kind = kind.name, dishId = dishId, startedAt = startedAt, step = step)
        TimerTarget.Quick -> Destination.Timers
    }
