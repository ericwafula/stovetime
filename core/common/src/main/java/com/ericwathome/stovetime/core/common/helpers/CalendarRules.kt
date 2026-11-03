package com.ericwathome.stovetime.core.common.helpers

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

val FirstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY

private const val DAYS_IN_WEEK = 7

fun LocalDate.weekStart(): LocalDate {
    val daysSinceStart = (dayOfWeek.isoDayNumber - FirstDayOfWeek.isoDayNumber + DAYS_IN_WEEK) % DAYS_IN_WEEK
    return minus(daysSinceStart, DateTimeUnit.DAY)
}

fun LocalDate.weekDays(): List<LocalDate> {
    val start = weekStart()
    return List(DAYS_IN_WEEK) { start.plus(it, DateTimeUnit.DAY) }
}

fun LocalDate.previousWeekStart(): LocalDate = weekStart().minus(DAYS_IN_WEEK, DateTimeUnit.DAY)

sealed interface RelativeDay {
    data object Today : RelativeDay

    data object Yesterday : RelativeDay

    data class ThisWeek(
        val dayOfWeek: DayOfWeek,
    ) : RelativeDay

    data class Earlier(
        val date: LocalDate,
        val isThisYear: Boolean,
    ) : RelativeDay
}

fun relativeDay(
    date: LocalDate,
    today: LocalDate,
): RelativeDay =
    when {
        date == today -> RelativeDay.Today
        date == today.minus(1, DateTimeUnit.DAY) -> RelativeDay.Yesterday
        date < today && date.weekStart() == today.weekStart() -> RelativeDay.ThisWeek(date.dayOfWeek)
        else -> RelativeDay.Earlier(date, isThisYear = date.year == today.year)
    }
