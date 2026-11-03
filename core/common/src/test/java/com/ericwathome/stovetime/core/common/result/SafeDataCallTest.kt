package com.ericwathome.stovetime.core.common.result

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class SafeDataCallTest {
    @Test
    fun a_successful_call_returns_its_data() =
        runTest {
            // when
            val result = safeDataCall(StandardTestDispatcher(testScheduler)) { 42 }

            // then
            assertThat(result).isEqualTo(DataResult.Success(42))
        }

    @Test
    fun a_failed_call_becomes_a_typed_error() =
        runTest {
            // when
            val result = safeDataCall<Int>(StandardTestDispatcher(testScheduler)) { throw IOException("disk") }

            // then
            assertThat(result).isEqualTo(DataResult.Error(DataError.Unknown("disk")))
        }

    @Test(expected = CancellationException::class)
    fun a_cancelled_call_is_not_turned_into_an_error() =
        runTest {
            safeDataCall<Int>(StandardTestDispatcher(testScheduler)) { throw CancellationException("left") }
        }

    @Test
    fun map_transforms_success_and_keeps_errors() {
        // given
        val error: DataResult<Int> = DataResult.Error(DataError.Timeout)

        // then
        assertThat(DataResult.Success(2).map { it * 2 }).isEqualTo(DataResult.Success(4))
        assertThat(error.map { it * 2 }).isEqualTo(error)
    }

    @Test
    fun on_success_and_on_error_run_only_for_their_case() {
        // given
        var seen = ""

        // when
        DataResult.Success(1).onSuccess { seen += "s" }.onError { seen += "e" }
        DataResult.Error(DataError.NoConnection).onSuccess { seen += "s" }.onError { seen += "e" }

        // then
        assertThat(seen).isEqualTo("se")
    }
}
