package com.ericwathome.stovetime.core.domain.rules

import com.ericwathome.stovetime.core.common.helpers.weekStart
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

internal val REMINDER_DAY = DayOfWeek.SATURDAY
internal val REMINDER_FROM = LocalTime(hour = 17, minute = 0)
internal val REMINDER_COOKS_LEFT = 1..2

internal fun weeklyReminder(
    now: LocalDateTime,
    thisWeek: WeekProgress,
    weekStreak: Int,
    enabled: Boolean,
    lastSentWeekStart: LocalDate?,
): WeeklyReminder? {
    val weekStart = now.date.weekStart()
    val cooksLeft = thisWeek.goal - thisWeek.cooks
    val isDue =
        enabled &&
            now.date.dayOfWeek == REMINDER_DAY &&
            now.time >= REMINDER_FROM &&
            cooksLeft in REMINDER_COOKS_LEFT &&
            lastSentWeekStart != weekStart
    return if (isDue) WeeklyReminder(cooksLeft = cooksLeft, weekStreak = weekStreak, weekStart = weekStart) else null
}
