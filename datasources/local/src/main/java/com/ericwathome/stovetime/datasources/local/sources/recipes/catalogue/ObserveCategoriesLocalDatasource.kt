package com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CatalogueDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.toLocalError
import com.ericwathome.stovetime.datasources.local.mappers.toCache
import com.ericwathome.stovetime.datasources.local.models.CategoryCache
import com.ericwathome.stovetime.datasources.local.models.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun interface ObserveCategoriesLocalDatasource {
    operator fun invoke(): Flow<LocalResult<List<CategoryCache>>>
}

internal class ObserveCategoriesLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val catalogueDao: CatalogueDao,
) : ObserveCategoriesLocalDatasource {
    override fun invoke(): Flow<LocalResult<List<CategoryCache>>> =
        catalogueDao
            .observeCategories()
            .map<List<CategoryEntity>, LocalResult<List<CategoryCache>>> { categories ->
                LocalResult.Success(categories.map { it.toCache() })
            }.catch { emit(LocalResult.Error(it.toLocalError())) }
            .flowOn(dispatchers.io)
}
