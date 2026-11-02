package com.ericwathome.stovetime.core.data.helpers

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.datasources.local.helpers.LocalError
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.network.helpers.NetworkError
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult

internal inline fun <T, R> LocalResult<T>.mapToData(transform: (T) -> R): DataResult<R> =
    when (this) {
        is LocalResult.Success -> DataResult.Success(transform(data))
        is LocalResult.Error -> DataResult.Error(error.toDataError())
    }

internal fun LocalError.toDataError(): DataError =
    when (this) {
        LocalError.StorageFull -> DataError.StorageFull
        is LocalError.Unknown -> DataError.Unknown(message)
    }

internal inline fun <T, R> NetworkResult<T>.mapToData(transform: (T) -> R): DataResult<R> =
    when (this) {
        is NetworkResult.Success -> DataResult.Success(transform(data))
        is NetworkResult.Error -> DataResult.Error(error.toDataError())
    }

internal fun NetworkError.toDataError(): DataError =
    when (this) {
        NetworkError.NoConnection -> DataError.NoConnection
        NetworkError.Timeout -> DataError.Timeout
        is NetworkError.Server -> DataError.Server(code)
        is NetworkError.Unknown -> DataError.Unknown(message)
    }
