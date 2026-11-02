package com.ericwathome.stovetime.core.data.repo.progress

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource

fun interface ClearProgressRepository {
    suspend operator fun invoke(): DataResult<Boolean>
}

internal class ClearProgressRepositoryImpl(
    private val cookLocalDatasource: CookLocalDatasource,
) : ClearProgressRepository {
    override suspend fun invoke(): DataResult<Boolean> = cookLocalDatasource.deleteAllCooks().mapToData { it }
}
