package com.ericwathome.stovetime.datasources.local.sources.progress.cook

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.mappers.toEntity
import com.ericwathome.stovetime.datasources.local.models.CookCache

fun interface SaveCookLocalDatasource {
    suspend operator fun invoke(cook: CookCache): LocalResult<Long>
}

internal class SaveCookLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val cookDao: CookDao,
) : SaveCookLocalDatasource {
    override suspend fun invoke(cook: CookCache): LocalResult<Long> =
        safeDatabaseCall(dispatchers.io) {
            cookDao.insertCook(cook.toEntity().copy(id = 0))
        }
}
