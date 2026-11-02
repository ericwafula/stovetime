package com.ericwathome.stovetime.core.domain.rules

import com.ericwathome.stovetime.core.common.helpers.previousWeekStart
import com.ericwathome.stovetime.core.common.helpers.weekDays
import com.ericwathome.stovetime.core.common.helpers.weekStart
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.WeekDay
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import kotlinx.datetime.LocalDate

internal const val PRACTICE_XP = 10
internal const val RECIPE_XP = 10

internal data class CookedDay(
    val date: LocalDate,
    val weeklyGoal: Int,
)

internal fun lessonXp(
    lessonXp: Int,
    cookedBefore: Boolean,
): Int = if (cookedBefore) PRACTICE_XP else lessonXp

internal fun lessonStates(
    lessonIds: List<String>,
    cookedLessonIds: Set<String>,
): List<LessonState> {
    val current = lessonIds.indexOfFirst { it !in cookedLessonIds }
    return lessonIds.mapIndexed { index, id ->
        when {
            id in cookedLessonIds -> LessonState.Done
            index == current -> LessonState.Current
            else -> LessonState.Locked
        }
    }
}

internal fun weekProgress(
    cooks: List<CookedDay>,
    today: LocalDate,
    goal: Int,
): WeekProgress {
    val days = today.weekDays()
    val cookedDates = cooks.map { it.date }.toSet()
    return WeekProgress(
        days = days.map { WeekDay(date = it, cooked = it in cookedDates, isToday = it == today) },
        cooks = cooks.count { it.date.weekStart() == today.weekStart() },
        goal = goal,
    )
}

private fun metWeeks(
    cooks: List<CookedDay>,
    today: LocalDate,
    currentGoal: Int,
): Set<LocalDate> {
    val thisWeek = today.weekStart()
    return cooks
        .groupBy { it.date.weekStart() }
        .filterKeys { it <= thisWeek }
        .filter { (week, weekCooks) ->
            val goal = if (week == thisWeek) currentGoal else weekCooks.maxBy { it.date }.weeklyGoal
            weekCooks.size >= goal
        }.keys
}

internal fun weekStreak(
    cooks: List<CookedDay>,
    today: LocalDate,
    currentGoal: Int,
): Int {
    val met = metWeeks(cooks, today, currentGoal)
    var week = if (today.weekStart() in met) today.weekStart() else today.previousWeekStart()
    var streak = 0
    while (week in met) {
        streak++
        week = week.previousWeekStart()
    }
    return streak
}

internal fun bestWeekStreak(
    cooks: List<CookedDay>,
    today: LocalDate,
    currentGoal: Int,
): Int {
    val met = metWeeks(cooks, today, currentGoal).sorted()
    var best = 0
    var run = 0
    var previous: LocalDate? = null
    met.forEach { week ->
        run = if (previous != null && week.previousWeekStart() == previous) run + 1 else 1
        best = maxOf(best, run)
        previous = week
    }
    return best
}
