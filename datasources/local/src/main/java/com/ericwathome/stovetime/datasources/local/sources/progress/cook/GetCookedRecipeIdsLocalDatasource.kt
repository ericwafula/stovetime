package com.ericwathome.stovetime.datasources.local.sources.progress.cook

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall

fun interface GetCookedRecipeIdsLocalDatasource {
    suspend operator fun invoke(): LocalResult<Set<String>>
}

internal class GetCookedRecipeIdsLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val cookDao: CookDao,
) : GetCookedRecipeIdsLocalDatasource {
    override suspend fun invoke(): LocalResult<Set<String>> =
        safeDatabaseCall(dispatchers.io) {
            cookDao.getCookedRecipeIds().toSet()
        }
}
