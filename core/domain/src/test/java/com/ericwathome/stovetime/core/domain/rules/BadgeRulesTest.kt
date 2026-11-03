package com.ericwathome.stovetime.core.domain.rules

import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.domain.model.BadgeKind
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BadgeRulesTest {
    private fun lesson(id: String) = Lesson(id, id, 10, Difficulty.Easy, 20, "lesson_$id", emptyList(), emptyList(), emptyList())

    private val courses =
        listOf(
            Course("home-basics", "Home basics", listOf(CourseUnit("eggs", 1, "Eggs", listOf(lesson("a"), lesson("b"))))),
            Course("kenyan", "Kenyan favourites", listOf(CourseUnit("kenyan", 1, "From the jiko", listOf(lesson("c"))))),
        )

    private fun earned(
        cooked: Set<String>,
        best: Int,
    ) = badges(courses, cooked, best).filter { it.earned }.map { it.id }

    @Test
    fun a_unit_badge_is_earned_on_its_last_lesson() {
        // then
        assertThat(earned(setOf("a"), best = 0)).isEmpty()
        assertThat(earned(setOf("a", "b"), best = 0)).containsExactly("unit:home-basics:eggs")
    }

    @Test
    fun streak_badges_come_at_two_four_and_eight_weeks() {
        // then
        assertThat(earned(emptySet(), best = 1)).isEmpty()
        assertThat(earned(emptySet(), best = 2)).containsExactly("streak:2")
        assertThat(earned(emptySet(), best = 4)).containsExactly("streak:2", "streak:4")
        assertThat(earned(emptySet(), best = 8)).containsExactly("streak:2", "streak:4", "streak:8")
    }

    @Test
    fun a_streak_badge_stays_after_the_streak_resets_because_it_follows_the_best_streak() {
        // given
        val currentStreak = 0
        val bestStreak = 4

        // then
        assertThat(currentStreak).isLessThan(2)
        assertThat(earned(emptySet(), best = bestStreak)).contains("streak:4")
    }

    @Test
    fun every_unit_and_streak_badge_is_listed_with_its_kind() {
        // when
        val all = badges(courses, emptySet(), 0)

        // then
        assertThat(all.map { it.kind })
            .containsExactly(
                BadgeKind.UnitDone("Home basics", "Eggs"),
                BadgeKind.UnitDone("Kenyan favourites", "From the jiko"),
                BadgeKind.Streak(2),
                BadgeKind.Streak(4),
                BadgeKind.Streak(8),
            ).inOrder()
    }
}
