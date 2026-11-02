package com.ericwathome.stovetime.datasources.local.sources.progress.cook

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall

fun interface DeleteAllCooksLocalDatasource {
    suspend operator fun invoke(): LocalResult<Boolean>
}

internal class DeleteAllCooksLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val cookDao: CookDao,
) : DeleteAllCooksLocalDatasource {
    override suspend fun invoke(): LocalResult<Boolean> =
        safeDatabaseCall(dispatchers.io) {
            cookDao.deleteAllCooks()
            true
        }
}
