package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.network.dtos.MealsResponseDto
import com.ericwathome.stovetime.datasources.network.helpers.NetworkError
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import io.ktor.client.HttpClient

fun interface LookupMealRemoteDatasource {
    suspend operator fun invoke(id: String): NetworkResult<MealsResponseDto>
}

internal class LookupMealRemoteDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val client: HttpClient,
) : LookupMealRemoteDatasource {
    // TODO(workshop 6.1): GET Route.LookupMeal with the meal id as the "i" parameter, wrapped in safeApiCall on the io dispatcher
    override suspend fun invoke(id: String): NetworkResult<MealsResponseDto> =
        NetworkResult.Error(NetworkError.Unknown("Built live in the workshop"))
}
