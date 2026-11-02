package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.network.dtos.MealsResponseDto
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.Route
import com.ericwathome.stovetime.datasources.network.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

fun interface LookupMealRemoteDatasource {
    suspend operator fun invoke(id: String): NetworkResult<MealsResponseDto>
}

internal class LookupMealRemoteDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val client: HttpClient,
) : LookupMealRemoteDatasource {
    override suspend fun invoke(id: String): NetworkResult<MealsResponseDto> =
        safeApiCall(coroutineContext = dispatchers.io) {
            client.get(urlString = Route.LookupMeal.url) {
                parameter("i", id)
            }
        }
}
