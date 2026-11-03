package com.ericwathome.stovetime.core.common.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ElapsedRulesTest {
    private val now = 1_800_000_000_000L

    @Test
    fun under_a_minute_is_just_now() {
        // when
        val elapsed = elapsedBetween(now - 30.seconds.inWholeMilliseconds, now)

        // then
        assertThat(elapsed).isEqualTo(Elapsed.JustNow)
    }

    @Test
    fun under_an_hour_counts_minutes() {
        // when
        val elapsed = elapsedBetween(now - 42.minutes.inWholeMilliseconds, now)

        // then
        assertThat(elapsed).isEqualTo(Elapsed.Minutes(42))
    }

    @Test
    fun under_a_day_counts_hours() {
        // when
        val elapsed = elapsedBetween(now - 2.hours.inWholeMilliseconds, now)

        // then
        assertThat(elapsed).isEqualTo(Elapsed.Hours(2))
    }

    @Test
    fun a_day_or_more_counts_days() {
        // when
        val elapsed = elapsedBetween(now - 2.days.inWholeMilliseconds, now)

        // then
        assertThat(elapsed).isEqualTo(Elapsed.Days(2))
    }

    @Test
    fun a_time_in_the_future_is_just_now() {
        // when
        val elapsed = elapsedBetween(now + 5.minutes.inWholeMilliseconds, now)

        // then
        assertThat(elapsed).isEqualTo(Elapsed.JustNow)
    }
}
