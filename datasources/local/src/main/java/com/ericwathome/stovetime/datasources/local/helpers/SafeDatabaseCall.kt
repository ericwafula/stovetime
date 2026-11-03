package com.ericwathome.stovetime.datasources.local.helpers

import android.database.sqlite.SQLiteFullException
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal const val DATABASE_ERROR_MESSAGE = "Unable to read or save data on the phone"

suspend inline fun <T> safeDatabaseCall(
    coroutineContext: CoroutineContext,
    crossinline block: suspend () -> T,
): LocalResult<T> =
    withContext(coroutineContext) {
        try {
            LocalResult.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: SQLiteFullException) {
            Timber.e(e)
            LocalResult.Error(LocalError.StorageFull)
        } catch (e: Exception) {
            Timber.e(e)
            LocalResult.Error(LocalError.Unknown(e.message ?: DATABASE_ERROR_MESSAGE))
        }
    }

fun Throwable.toLocalError(): LocalError =
    if (this is SQLiteFullException) LocalError.StorageFull else LocalError.Unknown(message ?: DATABASE_ERROR_MESSAGE)
