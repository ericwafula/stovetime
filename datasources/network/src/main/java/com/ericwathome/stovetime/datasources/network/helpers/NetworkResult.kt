package com.ericwathome.stovetime.datasources.network.helpers

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T,
    ) : NetworkResult<T>

    data class Error(
        val error: NetworkError,
    ) : NetworkResult<Nothing>
}

sealed interface NetworkError {
    data object NoConnection : NetworkError

    data object Timeout : NetworkError

    data class Server(
        val code: Int,
    ) : NetworkError

    data class Unknown(
        val message: String,
    ) : NetworkError
}
