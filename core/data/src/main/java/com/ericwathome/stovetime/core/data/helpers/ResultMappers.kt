package com.ericwathome.stovetime.core.data.helpers

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.datasources.local.helpers.LocalError
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult

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
