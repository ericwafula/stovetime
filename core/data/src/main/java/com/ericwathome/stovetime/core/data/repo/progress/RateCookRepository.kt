package com.ericwathome.stovetime.core.data.repo.progress

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource

fun interface RateCookRepository {
    suspend operator fun invoke(
        cookId: Long,
        rating: Rating?,
    ): DataResult<Boolean>
}

internal class RateCookRepositoryImpl(
    private val cookLocalDatasource: CookLocalDatasource,
) : RateCookRepository {
    override suspend fun invoke(
        cookId: Long,
        rating: Rating?,
    ): DataResult<Boolean> = cookLocalDatasource.rateCook(cookId, rating?.name).mapToData { it }
}
