package com.ericwathome.stovetime.datasources.network.helpers

import com.ericwathome.stovetime.datasources.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

private const val REQUEST_TIMEOUT_MILLIS = 15_000L
private const val CONNECT_TIMEOUT_MILLIS = 10_000L
private const val SOCKET_TIMEOUT_MILLIS = 15_000L

internal fun createHttpClient(
    engine: HttpClientEngine,
    isDebug: Boolean = BuildConfig.DEBUG,
): HttpClient =
    HttpClient(engine) {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
        }
        if (isDebug) {
            install(Logging) {
                level = LogLevel.INFO
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            Timber.d(message)
                        }
                    }
            }
        }
    }
