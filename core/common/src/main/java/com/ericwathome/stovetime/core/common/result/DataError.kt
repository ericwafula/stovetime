package com.ericwathome.stovetime.core.common.result

sealed interface DataError {
    data object NoConnection : DataError

    data object Timeout : DataError

    data class Server(
        val code: Int,
    ) : DataError

    data object StorageFull : DataError

    data class Unknown(
        val message: String,
    ) : DataError
}
