package com.ericwathome.stovetime.widget

import com.ericwathome.stovetime.sdk.timer.CookTimer
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimersWidgetContentTest {
    private val now = 100_000L

    private fun running(
        id: String,
        endsAt: Long,
        payload: String? = "quick",
    ) = CookTimer(id, "Timer $id", null, 600_000, endsAtMillis = endsAt, pausedRemainingMillis = null, payload = payload, ringing = false)

    private fun paused(
        id: String,
        remaining: Long,
    ) = CookTimer(id, "Timer $id", null, 600_000, endsAtMillis = null, pausedRemainingMillis = remaining, payload = "quick", ringing = false)

    private fun ringing(id: String) = CookTimer(id, "Timer $id", null, 600_000, endsAtMillis = now, pausedRemainingMillis = null, payload = null, ringing = true)

    @Test
    fun no_timers_shows_the_empty_state() {
        // then
        assertThat(widgetRows(emptyList(), now)).isEmpty()
    }

    @Test
    fun timers_show_ringing_first_then_soonest_ending_then_paused() {
        // when
        val rows = widgetRows(listOf(paused("p", 30_000), running("late", 900_000), ringing("r")), now)

        // then
        assertThat(rows.map { it.timerId }).containsExactly("r", "late", "p").inOrder()
        assertThat(rows[0].state).isEqualTo(WidgetRowState.TimeUp)
        assertThat(rows[1].state).isEqualTo(WidgetRowState.Running(800_000))
        assertThat(rows[2].state).isEqualTo(WidgetRowState.Paused(30_000))
    }

    @Test
    fun at_most_three_timers_show() {
        // when
        val rows = widgetRows(listOf(running("a", 400_000), running("b", 200_000), running("c", 300_000), running("d", 150_000)), now)

        // then
        assertThat(rows.map { it.timerId }).containsExactly("d", "b", "c").inOrder()
    }

    @Test
    fun a_timer_taps_through_to_the_same_place_as_its_notification() {
        // given
        val payload = "cook|Lesson|boiled-eggs|1000|1"

        // when
        val row = widgetRows(listOf(running("a", 400_000, payload = payload)), now).single()

        // then
        assertThat(row.payload).isEqualTo(payload)
    }

    @Test
    fun a_timer_without_a_payload_opens_the_timers_tab() {
        // then
        assertThat(widgetRows(listOf(ringing("r")), now).single().payload).isEqualTo(OPEN_TIMERS_PAYLOAD)
    }
}
