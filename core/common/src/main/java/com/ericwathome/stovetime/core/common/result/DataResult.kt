package com.ericwathome.stovetime.core.common.result

sealed interface DataResult<out T> {
    data class Success<T>(
        val data: T,
    ) : DataResult<T>

    data class Error(
        val error: DataError,
    ) : DataResult<Nothing>
}

inline fun <T, R> DataResult<T>.map(transform: (T) -> R): DataResult<R> =
    when (this) {
        is DataResult.Success -> DataResult.Success(transform(data))
        is DataResult.Error -> this
    }

inline fun <T> DataResult<T>.onSuccess(block: (T) -> Unit): DataResult<T> {
    if (this is DataResult.Success) block(data)
    return this
}

inline fun <T> DataResult<T>.onError(block: (DataError) -> Unit): DataResult<T> {
    if (this is DataResult.Error) block(error)
    return this
}

fun <T> DataResult<T>.getOrNull(): T? = (this as? DataResult.Success)?.data
