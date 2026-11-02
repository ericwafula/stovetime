package com.ericwathome.stovetime.core.domain.rules

import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.domain.model.Badge
import com.ericwathome.stovetime.core.domain.model.BadgeKind

internal val STREAK_BADGE_WEEKS = listOf(2, 4, 8)

internal fun badges(
    courses: List<Course>,
    cookedLessonIds: Set<String>,
    bestWeekStreak: Int,
): List<Badge> {
    val unitBadges =
        courses.flatMap { course ->
            course.units.map { unit ->
                Badge(
                    id = "unit:${course.id}:${unit.id}",
                    kind = BadgeKind.UnitDone(courseTitle = course.title, unitTitle = unit.title),
                    earned = unit.lessons.isNotEmpty() && unit.lessons.all { it.id in cookedLessonIds },
                )
            }
        }
    val streakBadges =
        STREAK_BADGE_WEEKS.map { weeks ->
            Badge(id = "streak:$weeks", kind = BadgeKind.Streak(weeks), earned = bestWeekStreak >= weeks)
        }
    return unitBadges + streakBadges
}
