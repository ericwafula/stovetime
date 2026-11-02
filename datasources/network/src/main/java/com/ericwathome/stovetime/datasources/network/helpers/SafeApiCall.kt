package com.ericwathome.stovetime.datasources.network.helpers

import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

internal suspend inline fun <reified T> safeApiCall(
    coroutineContext: CoroutineContext,
    crossinline execute: suspend () -> HttpResponse,
): NetworkResult<T> =
    withContext(coroutineContext) {
        try {
            val response = execute()
            if (response.status.isSuccess()) {
                NetworkResult.Success(response.body<T>())
            } else {
                NetworkResult.Error(NetworkError.Server(response.status.value))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            NetworkResult.Error(e.toNetworkError())
        }
    }

@PublishedApi
internal fun Exception.toNetworkError(): NetworkError =
    when (this) {
        is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> NetworkError.Timeout
        is UnresolvedAddressException, is UnknownHostException, is ConnectException -> NetworkError.NoConnection
        is ResponseException -> NetworkError.Server(response.status.value)
        else -> NetworkError.Unknown(message.orEmpty())
    }
