package com.ericwathome.stovetime.reminders

import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import org.junit.Test

class WeeklyReminderTextTest {
    private fun reminder(
        cooksLeft: Int,
        weekStreak: Int,
    ) = WeeklyReminder(cooksLeft = cooksLeft, weekStreak = weekStreak, weekStart = LocalDate(2026, 11, 2))

    @Test
    fun one_cook_left_with_a_streak_names_the_streak() {
        // then
        assertThat(weeklyReminderText(reminder(cooksLeft = 1, weekStreak = 3)))
            .isEqualTo(WeeklyReminderText(R.string.reminder_one_more_cook_streak, listOf(3)))
    }

    @Test
    fun two_cooks_left_with_a_streak_names_the_streak() {
        // then
        assertThat(weeklyReminderText(reminder(cooksLeft = 2, weekStreak = 5)))
            .isEqualTo(WeeklyReminderText(R.string.reminder_two_more_cooks_streak, listOf(5)))
    }

    @Test
    fun one_cook_left_without_a_streak_talks_about_the_goal() {
        // then
        assertThat(weeklyReminderText(reminder(cooksLeft = 1, weekStreak = 0)))
            .isEqualTo(WeeklyReminderText(R.string.reminder_one_more_cook_goal))
    }

    @Test
    fun two_cooks_left_without_a_streak_talks_about_the_goal() {
        // then
        assertThat(weeklyReminderText(reminder(cooksLeft = 2, weekStreak = 0)))
            .isEqualTo(WeeklyReminderText(R.string.reminder_two_more_cooks_goal))
    }
}
