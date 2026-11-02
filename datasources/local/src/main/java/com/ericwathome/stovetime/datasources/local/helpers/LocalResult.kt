package com.ericwathome.stovetime.datasources.local.helpers

sealed interface LocalResult<out T> {
    data class Success<T>(
        val data: T,
    ) : LocalResult<T>

    data class Error(
        val error: LocalError,
    ) : LocalResult<Nothing>
}

sealed interface LocalError {
    data object StorageFull : LocalError

    data class Unknown(
        val message: String,
    ) : LocalError
}
