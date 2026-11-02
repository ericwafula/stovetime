package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.network.dtos.MealsResponseDto
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.Route
import com.ericwathome.stovetime.datasources.network.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

fun interface GetMealsByCategoryRemoteDatasource {
    suspend operator fun invoke(category: String): NetworkResult<MealsResponseDto>
}

internal class GetMealsByCategoryRemoteDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val client: HttpClient,
) : GetMealsByCategoryRemoteDatasource {
    override suspend fun invoke(category: String): NetworkResult<MealsResponseDto> =
        safeApiCall(coroutineContext = dispatchers.io) {
            client.get(urlString = Route.FilterMeals.url) {
                parameter("c", category)
            }
        }
}
