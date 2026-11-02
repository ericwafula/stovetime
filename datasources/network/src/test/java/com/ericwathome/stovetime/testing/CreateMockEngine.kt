package com.ericwathome.stovetime.testing

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

fun createMockEngine(
    responseBody: String = "",
    status: HttpStatusCode = HttpStatusCode.OK,
    throwable: Throwable? = null,
    onRequest: (HttpRequestData) -> Unit = {},
): MockEngine =
    MockEngine { request ->
        onRequest(request)
        throwable?.let { throw it }
        respond(
            content = responseBody,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
