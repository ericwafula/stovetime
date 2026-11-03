package com.ericwathome.stovetime.core.data.model.timers

import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimerTargetTest {
    @Test
    fun a_cook_step_target_survives_encoding() {
        // given
        val target = TimerTarget.CookStep(kind = CookKind.Recipe, dishId = "52772", startedAt = 1_793_000_000_000, step = 3)

        // then
        assertThat(TimerTarget.decode(target.encoded)).isEqualTo(target)
    }

    @Test
    fun a_quick_target_survives_encoding() {
        // then
        assertThat(TimerTarget.decode(TimerTarget.Quick.encoded)).isEqualTo(TimerTarget.Quick)
    }

    @Test
    fun a_missing_or_broken_payload_falls_back_to_a_quick_timer() {
        // then
        assertThat(TimerTarget.decode(null)).isEqualTo(TimerTarget.Quick)
        assertThat(TimerTarget.decode("")).isEqualTo(TimerTarget.Quick)
        assertThat(TimerTarget.decode("cook|Soup|x|1|2")).isEqualTo(TimerTarget.Quick)
        assertThat(TimerTarget.decode("cook|Lesson|x|not-a-time|2")).isEqualTo(TimerTarget.Quick)
    }
}
