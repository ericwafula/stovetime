package com.ericwathome.stovetime.core.common.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NumberRulesTest {
    @Test
    fun a_six_and_a_half_minute_timer_reads_6_30() {
        // then
        assertThat(timerText(390_000)).isEqualTo("6:30")
    }

    @Test
    fun part_seconds_round_up_so_the_timer_never_shows_zero_early() {
        // then
        assertThat(timerText(400)).isEqualTo("0:01")
        assertThat(timerText(0)).isEqualTo("0:00")
    }

    @Test
    fun an_hour_or_more_shows_hours() {
        // then
        assertThat(timerText(3_900_000)).isEqualTo("1:05:00")
    }

    @Test
    fun negative_time_reads_zero() {
        // then
        assertThat(timerText(-5_000)).isEqualTo("0:00")
    }
}
