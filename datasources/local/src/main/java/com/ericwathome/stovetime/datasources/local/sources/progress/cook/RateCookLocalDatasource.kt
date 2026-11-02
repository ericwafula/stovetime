package com.ericwathome.stovetime.datasources.local.sources.progress.cook

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall

fun interface RateCookLocalDatasource {
    suspend operator fun invoke(
        id: Long,
        rating: String?,
    ): LocalResult<Boolean>
}

internal class RateCookLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val cookDao: CookDao,
) : RateCookLocalDatasource {
    override suspend fun invoke(
        id: Long,
        rating: String?,
    ): LocalResult<Boolean> =
        safeDatabaseCall(dispatchers.io) {
            cookDao.updateRating(id, rating)
            true
        }
}
