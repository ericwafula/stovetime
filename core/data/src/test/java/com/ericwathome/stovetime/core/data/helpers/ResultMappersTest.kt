package com.ericwathome.stovetime.core.data.helpers

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.datasources.network.helpers.NetworkError
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultMappersTest {
    @Test
    fun network_errors_map_to_data_errors() {
        // when
        val errors =
            listOf(NetworkError.NoConnection, NetworkError.Timeout, NetworkError.Server(503), NetworkError.Unknown("boom"))
                .map { it.toDataError() }

        // then
        assertThat(errors)
            .containsExactly(DataError.NoConnection, DataError.Timeout, DataError.Server(503), DataError.Unknown("boom"))
            .inOrder()
    }
}
