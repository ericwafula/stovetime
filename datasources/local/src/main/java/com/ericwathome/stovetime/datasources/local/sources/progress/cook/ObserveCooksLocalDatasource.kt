package com.ericwathome.stovetime.datasources.local.sources.progress.cook

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.toLocalError
import com.ericwathome.stovetime.datasources.local.mappers.toCache
import com.ericwathome.stovetime.datasources.local.models.CookCache
import com.ericwathome.stovetime.datasources.local.models.CookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun interface ObserveCooksLocalDatasource {
    operator fun invoke(): Flow<LocalResult<List<CookCache>>>
}

internal class ObserveCooksLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val cookDao: CookDao,
) : ObserveCooksLocalDatasource {
    override fun invoke(): Flow<LocalResult<List<CookCache>>> =
        cookDao
            .observeCooks()
            .map<List<CookEntity>, LocalResult<List<CookCache>>> { cooks ->
                LocalResult.Success(cooks.map { it.toCache() })
            }.catch { emit(LocalResult.Error(it.toLocalError())) }
            .flowOn(dispatchers.io)
}
