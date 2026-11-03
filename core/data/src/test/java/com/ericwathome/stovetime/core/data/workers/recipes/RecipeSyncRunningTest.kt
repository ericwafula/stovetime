package com.ericwathome.stovetime.core.data.workers.recipes

import androidx.work.WorkInfo
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecipeSyncRunningTest {
    @Test
    fun a_waiting_daily_sync_is_not_running() {
        // when
        val running = isRecipeSyncRunning(scheduled = listOf(WorkInfo.State.ENQUEUED), requested = emptyList())

        // then
        assertThat(running).isFalse()
    }

    @Test
    fun a_running_daily_sync_is_running() {
        // when
        val running = isRecipeSyncRunning(scheduled = listOf(WorkInfo.State.RUNNING), requested = emptyList())

        // then
        assertThat(running).isTrue()
    }

    @Test
    fun a_requested_sync_waiting_to_start_is_running() {
        // when
        val running = isRecipeSyncRunning(scheduled = listOf(WorkInfo.State.ENQUEUED), requested = listOf(WorkInfo.State.ENQUEUED))

        // then
        assertThat(running).isTrue()
    }

    @Test
    fun a_finished_requested_sync_is_not_running() {
        // when
        val running = isRecipeSyncRunning(scheduled = emptyList(), requested = listOf(WorkInfo.State.SUCCEEDED))

        // then
        assertThat(running).isFalse()
    }
}
