package com.ericwathome.stovetime.core.common.helpers

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

sealed interface Elapsed {
    data object JustNow : Elapsed

    data class Minutes(
        val count: Int,
    ) : Elapsed

    data class Hours(
        val count: Int,
    ) : Elapsed

    data class Days(
        val count: Int,
    ) : Elapsed
}

fun elapsedBetween(
    thenMillis: Long,
    nowMillis: Long,
): Elapsed {
    val elapsed = (nowMillis - thenMillis).coerceAtLeast(0).milliseconds
    return when {
        elapsed < 1.minutes -> Elapsed.JustNow
        elapsed < 1.hours -> Elapsed.Minutes(elapsed.inWholeMinutes.toInt())
        elapsed < 1.days -> Elapsed.Hours(elapsed.inWholeHours.toInt())
        else -> Elapsed.Days(elapsed.inWholeDays.toInt())
    }
}
