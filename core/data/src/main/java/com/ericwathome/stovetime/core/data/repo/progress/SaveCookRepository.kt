package com.ericwathome.stovetime.core.data.repo.progress

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.mappers.progress.toCache
import com.ericwathome.stovetime.core.data.model.progress.NewCook
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource

fun interface SaveCookRepository {
    suspend operator fun invoke(cook: NewCook): DataResult<Long>
}

internal class SaveCookRepositoryImpl(
    private val cookLocalDatasource: CookLocalDatasource,
) : SaveCookRepository {
    override suspend fun invoke(cook: NewCook): DataResult<Long> = cookLocalDatasource.saveCook(cook.toCache()).mapToData { it }
}
