package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.network.dtos.CategoriesResponseDto
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.Route
import com.ericwathome.stovetime.datasources.network.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

fun interface GetCategoriesRemoteDatasource {
    suspend operator fun invoke(): NetworkResult<CategoriesResponseDto>
}

internal class GetCategoriesRemoteDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val client: HttpClient,
) : GetCategoriesRemoteDatasource {
    override suspend fun invoke(): NetworkResult<CategoriesResponseDto> =
        safeApiCall(coroutineContext = dispatchers.io) {
            client.get(urlString = Route.Categories.url)
        }
}
