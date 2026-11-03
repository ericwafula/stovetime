package com.ericwathome.stovetime.core.data.helpers

import androidx.work.ListenableWorker
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WorkerHelpersTest {
    @Test
    fun a_successful_sync_succeeds() {
        // when
        val result = DataResult.Success(Unit).toWorkerResult(runAttemptCount = 0)

        // then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
    }

    @Test
    fun a_failed_sync_below_the_cap_retries() {
        // when
        val result = DataResult.Error(DataError.NoConnection).toWorkerResult(runAttemptCount = 0)

        // then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
    }

    @Test
    fun a_failed_sync_at_the_cap_gives_up() {
        // when
        val result = DataResult.Error(DataError.NoConnection).toWorkerResult(runAttemptCount = MAX_WORKER_ATTEMPTS - 1)

        // then
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }
}
