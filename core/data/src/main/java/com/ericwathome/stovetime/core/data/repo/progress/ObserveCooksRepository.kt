package com.ericwathome.stovetime.core.data.repo.progress

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.mappers.progress.toData
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun interface ObserveCooksRepository {
    operator fun invoke(): Flow<DataResult<List<Cook>>>
}

internal class ObserveCooksRepositoryImpl(
    private val cookLocalDatasource: CookLocalDatasource,
) : ObserveCooksRepository {
    override fun invoke(): Flow<DataResult<List<Cook>>> =
        cookLocalDatasource.observeCooks().map { result -> result.mapToData { cooks -> cooks.map { it.toData() } } }
}
