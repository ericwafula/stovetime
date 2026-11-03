package com.ericwathome.stovetime.navigation.entry

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.features.timers.timers.TimersRoute
import com.ericwathome.stovetime.navigation.Destination

fun navigationTimers(
    key: Destination.Timers,
    onOpenCookStep: (kind: CookKind, dishId: String, startedAt: Long, step: Int) -> Unit,
): NavEntry<NavKey> = NavEntry(key) { TimersRoute(onOpenCookStep = onOpenCookStep) }
