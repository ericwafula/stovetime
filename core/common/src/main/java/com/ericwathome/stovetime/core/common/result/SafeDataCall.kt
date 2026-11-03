package com.ericwathome.stovetime.core.common.result

import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safeDataCall(
    coroutineContext: CoroutineContext,
    crossinline block: suspend () -> T,
): DataResult<T> =
    withContext(coroutineContext) {
        try {
            DataResult.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            DataResult.Error(DataError.Unknown(e.message.orEmpty()))
        }
    }
