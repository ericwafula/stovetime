package com.ericwathome.stovetime.core.domain.rules

import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Test

class ReminderRulesTest {
    private val monday = LocalDate(2026, 11, 2)
    private val saturdayEvening = LocalDateTime(2026, 11, 7, 18, 0)

    private fun reminder(
        now: LocalDateTime = saturdayEvening,
        cooks: Int = 3,
        goal: Int = 4,
        weekStreak: Int = 3,
        enabled: Boolean = true,
        lastSentWeekStart: LocalDate? = null,
    ) = weeklyReminder(
        now = now,
        thisWeek = WeekProgress(days = emptyList(), cooks = cooks, goal = goal),
        weekStreak = weekStreak,
        enabled = enabled,
        lastSentWeekStart = lastSentWeekStart,
    )

    @Test
    fun one_cook_short_on_saturday_evening_reminds_with_the_streak() {
        // then
        assertThat(reminder()).isEqualTo(WeeklyReminder(cooksLeft = 1, weekStreak = 3, weekStart = monday))
    }

    @Test
    fun two_cooks_short_reminds_too() {
        // then
        assertThat(reminder(cooks = 2)?.cooksLeft).isEqualTo(2)
    }

    @Test
    fun three_or_more_cooks_short_stays_quiet() {
        // then
        assertThat(reminder(cooks = 1)).isNull()
        assertThat(reminder(cooks = 0)).isNull()
    }

    @Test
    fun a_met_goal_stays_quiet() {
        // then
        assertThat(reminder(cooks = 4)).isNull()
        assertThat(reminder(cooks = 6)).isNull()
    }

    @Test
    fun saturday_before_five_pm_stays_quiet() {
        // then
        assertThat(reminder(now = LocalDateTime(2026, 11, 7, 16, 59))).isNull()
    }

    @Test
    fun saturday_from_five_pm_to_midnight_reminds() {
        // then
        assertThat(reminder(now = LocalDateTime(2026, 11, 7, 17, 0))).isNotNull()
        assertThat(reminder(now = LocalDateTime(2026, 11, 7, 23, 59))).isNotNull()
    }

    @Test
    fun other_days_stay_quiet_even_in_the_evening() {
        // then
        (2..6).forEach { day -> assertThat(reminder(now = LocalDateTime(2026, 11, day, 18, 0))).isNull() }
        assertThat(reminder(now = LocalDateTime(2026, 11, 8, 18, 0))).isNull()
    }

    @Test
    fun a_reminder_already_sent_this_week_is_not_sent_again() {
        // then
        assertThat(reminder(lastSentWeekStart = monday)).isNull()
    }

    @Test
    fun a_reminder_sent_last_week_does_not_stop_this_one() {
        // then
        assertThat(reminder(lastSentWeekStart = LocalDate(2026, 10, 26))).isNotNull()
    }

    @Test
    fun the_switch_off_stays_quiet() {
        // then
        assertThat(reminder(enabled = false)).isNull()
    }

    @Test
    fun no_streak_yet_reminds_with_a_zero_streak() {
        // then
        assertThat(reminder(weekStreak = 0)?.weekStreak).isEqualTo(0)
    }
}
