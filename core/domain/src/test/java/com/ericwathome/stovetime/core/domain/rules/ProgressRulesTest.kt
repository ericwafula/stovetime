package com.ericwathome.stovetime.core.domain.rules

import com.ericwathome.stovetime.core.domain.model.LessonState
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import org.junit.Test

class ProgressRulesTest {
    private val wednesday = LocalDate(2026, 11, 4)

    private fun cooks(
        vararg dates: LocalDate,
        goal: Int = 4,
    ) = dates.map { CookedDay(it, goal) }

    private fun week(
        monday: LocalDate,
        count: Int,
        goal: Int = 1,
    ) = List(count) { CookedDay(monday, goal) }

    @Test
    fun a_first_cook_earns_the_lesson_xp() {
        // then
        assertThat(lessonXp(lessonXp = 30, cookedBefore = false)).isEqualTo(30)
    }

    @Test
    fun cooking_a_lesson_again_earns_practice_xp() {
        // then
        assertThat(lessonXp(lessonXp = 30, cookedBefore = true)).isEqualTo(10)
    }

    @Test
    fun a_fresh_path_has_the_first_lesson_current_and_the_rest_locked() {
        // when
        val states = lessonStates(listOf("a", "b", "c"), cookedLessonIds = emptySet())

        // then
        assertThat(states).containsExactly(LessonState.Current, LessonState.Locked, LessonState.Locked).inOrder()
    }

    @Test
    fun lessons_unlock_in_order() {
        // when
        val states = lessonStates(listOf("a", "b", "c"), cookedLessonIds = setOf("a"))

        // then
        assertThat(states).containsExactly(LessonState.Done, LessonState.Current, LessonState.Locked).inOrder()
    }

    @Test
    fun a_finished_unit_has_every_lesson_done_and_none_current() {
        // when
        val states = lessonStates(listOf("a", "b"), cookedLessonIds = setOf("a", "b"))

        // then
        assertThat(states).containsExactly(LessonState.Done, LessonState.Done).inOrder()
    }

    @Test
    fun a_cook_on_sunday_counts_for_the_week_that_started_on_monday() {
        // given
        val sunday = LocalDate(2026, 11, 8)

        // when
        val week = weekProgress(cooks(sunday), today = sunday, goal = 4)

        // then
        assertThat(week.cooks).isEqualTo(1)
        assertThat(week.days.last().cooked).isTrue()
    }

    @Test
    fun a_cook_on_monday_starts_a_new_week() {
        // given
        val sunday = LocalDate(2026, 11, 8)
        val monday = LocalDate(2026, 11, 9)

        // when
        val week = weekProgress(cooks(sunday), today = monday, goal = 4)

        // then
        assertThat(week.cooks).isEqualTo(0)
        assertThat(week.days.first().isToday).isTrue()
    }

    @Test
    fun the_week_row_highlights_today_and_marks_cooked_days() {
        // when
        val week = weekProgress(cooks(LocalDate(2026, 11, 2), wednesday), today = wednesday, goal = 4)

        // then
        assertThat(week.days.map { it.cooked }).containsExactly(true, false, true, false, false, false, false).inOrder()
        assertThat(week.days.indexOfFirst { it.isToday }).isEqualTo(2)
        assertThat(week.isGoalMet).isFalse()
    }

    @Test
    fun three_weeks_in_a_row_that_met_the_goal_make_a_streak_of_three() {
        // given
        val history =
            week(LocalDate(2026, 10, 19), count = 1) +
                week(LocalDate(2026, 10, 26), count = 1) +
                week(LocalDate(2026, 11, 2), count = 1)

        // then
        assertThat(weekStreak(history, today = wednesday, currentGoal = 1)).isEqualTo(3)
    }

    @Test
    fun a_missed_week_resets_the_streak_to_zero() {
        // given
        val history = week(LocalDate(2026, 10, 19), count = 1)

        // then
        assertThat(weekStreak(history, today = wednesday, currentGoal = 1)).isEqualTo(0)
    }

    @Test
    fun this_week_not_yet_met_keeps_last_weeks_streak() {
        // given
        val history = week(LocalDate(2026, 10, 19), count = 4, goal = 4) + week(LocalDate(2026, 10, 26), count = 4, goal = 4)

        // then
        assertThat(weekStreak(history + cooks(wednesday), today = wednesday, currentGoal = 4)).isEqualTo(2)
    }

    @Test
    fun this_week_adds_to_the_streak_as_soon_as_the_goal_is_met() {
        // given
        val history = week(LocalDate(2026, 10, 26), count = 2, goal = 2) + week(LocalDate(2026, 11, 2), count = 2, goal = 2)

        // then
        assertThat(weekStreak(history, today = wednesday, currentGoal = 2)).isEqualTo(2)
    }

    @Test
    fun a_past_week_is_judged_by_the_goal_it_had() {
        // given
        val lastWeek = week(LocalDate(2026, 10, 26), count = 2, goal = 2)

        // then
        assertThat(weekStreak(lastWeek, today = wednesday, currentGoal = 7)).isEqualTo(1)
    }

    @Test
    fun lowering_the_goal_mid_week_counts_this_week_as_met_straight_away() {
        // given
        val thisWeek = week(LocalDate(2026, 11, 2), count = 2, goal = 4)

        // then
        assertThat(weekStreak(thisWeek, today = wednesday, currentGoal = 2)).isEqualTo(1)
    }

    @Test
    fun the_best_streak_stays_after_a_reset() {
        // given
        val history =
            week(LocalDate(2026, 9, 28), count = 1) +
                week(LocalDate(2026, 10, 5), count = 1) +
                week(LocalDate(2026, 10, 12), count = 1)

        // then
        assertThat(weekStreak(history, today = wednesday, currentGoal = 1)).isEqualTo(0)
        assertThat(bestWeekStreak(history, today = wednesday, currentGoal = 1)).isEqualTo(3)
    }

    @Test
    fun no_cooks_means_no_streak() {
        // then
        assertThat(weekStreak(emptyList(), today = wednesday, currentGoal = 4)).isEqualTo(0)
        assertThat(bestWeekStreak(emptyList(), today = wednesday, currentGoal = 4)).isEqualTo(0)
    }
}
