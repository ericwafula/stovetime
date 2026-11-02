package com.ericwathome.stovetime.sdk.timer

import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TimerBookTest {
    private val now = 1_000_000_000L
    private val oneMinute = 1.minutes.inWholeMilliseconds
    private var nextId = 0
    private lateinit var sut: TimerBook

    @Before
    fun setup() {
        nextId = 0
        sut = TimerBook(newId = { "timer-${nextId++}" })
    }

    @Test
    fun two_timers_end_in_the_order_of_their_end_instants() =
        runTest {
            // given
            val pasta = sut.start("Pasta", 10.minutes.inWholeMilliseconds, null, null, now)
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)

            // when
            val firstToRing = sut.ringDue(now + 6.minutes.inWholeMilliseconds)
            val secondToRing = sut.ringDue(now + 10.minutes.inWholeMilliseconds)

            // then
            assertThat(firstToRing.map { it.id }).containsExactly(eggs.id)
            assertThat(secondToRing.map { it.id }).containsExactly(pasta.id)
        }

    @Test
    fun timers_are_listed_soonest_ending_first() =
        runTest {
            // when
            val pasta = sut.start("Pasta", 10.minutes.inWholeMilliseconds, null, null, now)
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)

            // then
            assertThat(sut.timers.map { it.id }).containsExactly(eggs.id, pasta.id).inOrder()
        }

    @Test
    fun paused_timers_are_listed_after_running_ones() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            val pasta = sut.start("Pasta", 10.minutes.inWholeMilliseconds, null, null, now)

            // when
            sut.pause(eggs.id, now)

            // then
            assertThat(sut.timers.map { it.id }).containsExactly(pasta.id, eggs.id).inOrder()
        }

    @Test
    fun pausing_then_resuming_keeps_the_time_left() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            val pausedAt = now + 2.minutes.inWholeMilliseconds
            val resumedAt = pausedAt + 30.minutes.inWholeMilliseconds

            // when
            sut.pause(eggs.id, pausedAt)
            val whilePaused = sut.timers.single().remainingMillis(resumedAt)
            sut.resume(eggs.id, resumedAt)

            // then
            val resumed = sut.timers.single()
            assertThat(whilePaused).isEqualTo(4.minutes.inWholeMilliseconds)
            assertThat(resumed.isPaused).isFalse()
            assertThat(resumed.endsAtMillis).isEqualTo(resumedAt + 4.minutes.inWholeMilliseconds)
        }

    @Test
    fun adding_a_minute_to_a_running_timer_moves_its_end_by_sixty_seconds() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)

            // when
            sut.addMinute(eggs.id, now + 1.minutes.inWholeMilliseconds)

            // then
            val timer = sut.timers.single()
            assertThat(timer.endsAtMillis).isEqualTo(eggs.endsAtMillis!! + oneMinute)
            assertThat(timer.durationMillis).isEqualTo(7.minutes.inWholeMilliseconds)
        }

    @Test
    fun adding_a_minute_to_a_paused_timer_adds_to_its_time_left() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            sut.pause(eggs.id, now + 2.minutes.inWholeMilliseconds)

            // when
            sut.addMinute(eggs.id, now + 5.minutes.inWholeMilliseconds)

            // then
            val timer = sut.timers.single()
            assertThat(timer.pausedRemainingMillis).isEqualTo(5.minutes.inWholeMilliseconds)
            assertThat(timer.durationMillis).isEqualTo(7.minutes.inWholeMilliseconds)
        }

    @Test
    fun cancelling_a_timer_removes_it() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            val pasta = sut.start("Pasta", 10.minutes.inWholeMilliseconds, null, null, now)

            // when
            sut.cancel(eggs.id)

            // then
            assertThat(sut.timers.map { it.id }).containsExactly(pasta.id)
        }

    @Test
    fun restoring_after_the_app_is_killed_rebuilds_the_same_timers() =
        runTest {
            // given
            sut.start("Eggs", 6.minutes.inWholeMilliseconds, "for a jammy, set-at-the-edges yolk", "lesson-1/step-3", now)
            val pasta = sut.start("Pasta", 10.minutes.inWholeMilliseconds, null, null, now)
            sut.pause(pasta.id, now + 1.minutes.inWholeMilliseconds)
            val persisted = sut.toJson()

            // when
            val restored = TimerBook.fromJson(persisted)

            // then
            assertThat(restored.timers).isEqualTo(sut.timers)
        }

    @Test
    fun a_timer_that_ended_while_the_app_was_dead_rings_straight_away_on_restore() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            sut.start("Pasta", 10.minutes.inWholeMilliseconds, null, null, now)
            val restored = TimerBook.fromJson(sut.toJson())

            // when
            val ringing = restored.ringDue(now + 8.minutes.inWholeMilliseconds)

            // then
            assertThat(ringing.map { it.id }).containsExactly(eggs.id)
            assertThat(restored.timers.first { it.id == eggs.id }.ringing).isTrue()
        }

    @Test
    fun a_timer_that_already_rang_before_the_app_was_killed_does_not_ring_again_on_restore() =
        runTest {
            // given
            sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            sut.ringDue(now + 6.minutes.inWholeMilliseconds)
            val restored = TimerBook.fromJson(sut.toJson())

            // when
            val ringing = restored.ringDue(now + 8.minutes.inWholeMilliseconds)

            // then
            assertThat(ringing).isEmpty()
            assertThat(restored.timers.single().ringing).isTrue()
        }

    @Test
    fun restoring_unreadable_json_gives_no_timers() =
        runTest {
            // when
            val restored = TimerBook.fromJson("not json")

            // then
            assertThat(restored.timers).isEmpty()
        }

    @Test
    fun two_timers_ending_together_both_ring() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            val rice = sut.start("Rice", 6.minutes.inWholeMilliseconds, null, null, now)

            // when
            val ringing = sut.ringDue(now + 6.minutes.inWholeMilliseconds)

            // then
            assertThat(ringing.map { it.id }).containsExactly(eggs.id, rice.id)
        }

    @Test
    fun snoozing_a_ringing_timer_restarts_it_for_exactly_one_minute() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            val endedAt = now + 6.minutes.inWholeMilliseconds
            sut.ringDue(endedAt)
            val snoozedAt = endedAt + 20.seconds.inWholeMilliseconds

            // when
            sut.snooze(eggs.id, snoozedAt)

            // then
            val timer = sut.timers.single()
            assertThat(timer.ringing).isFalse()
            assertThat(timer.endsAtMillis).isEqualTo(snoozedAt + oneMinute)
            assertThat(timer.durationMillis).isEqualTo(oneMinute)
        }

    @Test
    fun stopping_a_ringing_timer_clears_it() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)
            sut.ringDue(now + 6.minutes.inWholeMilliseconds)

            // when
            sut.stopRinging(eggs.id)

            // then
            assertThat(sut.timers).isEmpty()
        }

    @Test
    fun time_left_is_never_negative() =
        runTest {
            // given
            val eggs = sut.start("Eggs", 6.minutes.inWholeMilliseconds, null, null, now)

            // when
            val remaining = eggs.remainingMillis(now + 1.hours.inWholeMilliseconds)

            // then
            assertThat(remaining).isEqualTo(0L)
        }
}
