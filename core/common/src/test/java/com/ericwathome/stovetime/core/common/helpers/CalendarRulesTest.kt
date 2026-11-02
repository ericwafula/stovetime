package com.ericwathome.stovetime.core.common.helpers

import com.google.common.truth.Truth.assertThat
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
}
