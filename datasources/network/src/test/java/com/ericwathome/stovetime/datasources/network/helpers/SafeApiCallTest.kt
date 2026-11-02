package com.ericwathome.stovetime.datasources.network.helpers

import com.google.common.truth.Truth.assertThat
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException

class SafeApiCallTest {
    @Test
    fun an_unknown_host_is_no_connection() {
        assertThat(UnknownHostException("www.themealdb.com").toNetworkError()).isEqualTo(NetworkError.NoConnection)
    }

    @Test
    fun a_refused_connection_is_no_connection() {
        assertThat(ConnectException("refused").toNetworkError()).isEqualTo(NetworkError.NoConnection)
    }

    @Test
    fun a_connect_timeout_is_a_timeout_not_no_connection() {
        assertThat(ConnectTimeoutException("connect", null).toNetworkError()).isEqualTo(NetworkError.Timeout)
    }

    @Test
    fun a_socket_timeout_is_a_timeout() {
        assertThat(SocketTimeoutException("read", null).toNetworkError()).isEqualTo(NetworkError.Timeout)
    }

    @Test
    fun any_other_failure_is_unknown_with_its_message() {
        assertThat(IOException("broken pipe").toNetworkError()).isEqualTo(NetworkError.Unknown("broken pipe"))
    }
}
