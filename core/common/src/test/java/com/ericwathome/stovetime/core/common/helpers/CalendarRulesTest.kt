package com.ericwathome.stovetime.core.common.helpers

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.Test

class CalendarRulesTest {
    @Test
    fun a_sunday_belongs_to_the_week_that_started_on_the_monday_before() {
        // then
        assertThat(LocalDate(2026, 11, 8).weekStart()).isEqualTo(LocalDate(2026, 11, 2))
    }

    @Test
    fun a_monday_starts_its_own_week() {
        // then
        assertThat(LocalDate(2026, 11, 9).weekStart()).isEqualTo(LocalDate(2026, 11, 9))
    }

    @Test
    fun the_week_runs_monday_to_sunday() {
        // when
        val days = LocalDate(2026, 11, 4).weekDays()

        // then
        assertThat(days.first()).isEqualTo(LocalDate(2026, 11, 2))
        assertThat(days.last()).isEqualTo(LocalDate(2026, 11, 8))
        assertThat(days).hasSize(7)
    }

    @Test
    fun the_previous_week_starts_seven_days_earlier() {
        // then
        assertThat(LocalDate(2026, 11, 4).previousWeekStart()).isEqualTo(LocalDate(2026, 10, 26))
    }

    @Test
    fun the_same_day_is_today() {
        // then
        assertThat(relativeDay(LocalDate(2026, 11, 4), today = LocalDate(2026, 11, 4))).isEqualTo(RelativeDay.Today)
    }

    @Test
    fun the_day_before_is_yesterday_even_across_a_week_boundary() {
        // then
        assertThat(relativeDay(LocalDate(2026, 11, 8), today = LocalDate(2026, 11, 9))).isEqualTo(RelativeDay.Yesterday)
    }

    @Test
    fun an_earlier_day_this_week_is_named_by_its_weekday() {
        // then
        assertThat(relativeDay(LocalDate(2026, 11, 2), today = LocalDate(2026, 11, 5)))
            .isEqualTo(RelativeDay.ThisWeek(DayOfWeek.MONDAY))
    }

    @Test
    fun a_day_from_last_week_is_an_earlier_date() {
        // then
        assertThat(relativeDay(LocalDate(2026, 11, 7), today = LocalDate(2026, 11, 10)))
            .isEqualTo(RelativeDay.Earlier(LocalDate(2026, 11, 7), isThisYear = true))
    }

    @Test
    fun a_day_from_last_year_says_so() {
        // then
        assertThat(relativeDay(LocalDate(2025, 12, 1), today = LocalDate(2026, 1, 8)))
            .isEqualTo(RelativeDay.Earlier(LocalDate(2025, 12, 1), isThisYear = false))
    }
}
