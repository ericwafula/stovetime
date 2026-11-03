package com.ericwathome.stovetime.notifications

import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.navigation.Destination
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotificationDestinationsTest {
    @Test
    fun a_step_timer_opens_its_cook_step() {
        // when
        val destination = TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", 1_000, 1).toDestination()

        // then
        assertThat(destination).isEqualTo(Destination.Cook(kind = "Lesson", dishId = "jammy-eggs-on-toast", startedAt = 1_000, step = 1))
    }

    @Test
    fun a_quick_timer_opens_the_timers_tab() {
        // then
        assertThat(TimerTarget.Quick.toDestination()).isEqualTo(Destination.Timers)
    }
}
