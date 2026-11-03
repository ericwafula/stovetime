package com.ericwathome.stovetime.sdk.timer

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AlarmPlansTest {
    private fun running(
        id: String,
        endsAt: Long,
    ) = CookTimer(id, id, null, 60_000, endsAtMillis = endsAt, pausedRemainingMillis = null, payload = null, ringing = false)

    private fun paused(id: String) = CookTimer(id, id, null, 60_000, endsAtMillis = null, pausedRemainingMillis = 30_000, payload = null, ringing = false)

    @Test
    fun a_new_timer_gets_an_alarm() {
        // when
        val changes = alarmChanges(before = emptyList(), after = listOf(running("a", 5_000)))

        // then
        assertThat(changes.schedule).containsExactly("a", 5_000L)
        assertThat(changes.cancel).isEmpty()
    }

    @Test
    fun an_unchanged_timer_is_not_rescheduled() {
        // when
        val changes = alarmChanges(before = listOf(running("a", 5_000)), after = listOf(running("a", 5_000)))

        // then
        assertThat(changes.schedule).isEmpty()
        assertThat(changes.cancel).isEmpty()
    }

    @Test
    fun adding_a_minute_moves_the_alarm() {
        // when
        val changes = alarmChanges(before = listOf(running("a", 5_000)), after = listOf(running("a", 65_000)))

        // then
        assertThat(changes.schedule).containsExactly("a", 65_000L)
    }

    @Test
    fun pausing_or_cancelling_a_timer_cancels_its_alarm() {
        // when
        val changes = alarmChanges(before = listOf(running("a", 5_000), running("b", 9_000)), after = listOf(paused("a")))

        // then
        assertThat(changes.cancel).containsExactly("a", "b")
        assertThat(changes.schedule).isEmpty()
    }

    @Test
    fun a_ringing_timer_keeps_sounding_for_a_minute_then_goes_quiet() {
        // given
        val soundingUntil = mapOf("a" to 60_000L, "b" to 90_000L)

        // when
        val atFiftyNineSeconds = soundPlan(soundingUntil, nowMillis = 59_999)
        val atOneMinute = soundPlan(soundingUntil, nowMillis = 60_000)

        // then
        assertThat(atFiftyNineSeconds.silenced).isEmpty()
        assertThat(atFiftyNineSeconds.nextSilenceAt).isEqualTo(60_000)
        assertThat(atOneMinute.silenced).containsExactly("a")
        assertThat(atOneMinute.nextSilenceAt).isEqualTo(90_000)
    }

    @Test
    fun nothing_sounding_means_no_next_silence() {
        // then
        assertThat(soundPlan(emptyMap(), nowMillis = 0).nextSilenceAt).isNull()
    }
}
