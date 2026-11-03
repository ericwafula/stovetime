package com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CatalogueDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.toLocalError
import com.ericwathome.stovetime.datasources.local.mappers.toCache
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun interface ObserveCatalogueMealsLocalDatasource {
    operator fun invoke(): Flow<LocalResult<List<CatalogueMealCache>>>
}

internal class ObserveCatalogueMealsLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val catalogueDao: CatalogueDao,
) : ObserveCatalogueMealsLocalDatasource {
    override fun invoke(): Flow<LocalResult<List<CatalogueMealCache>>> =
        catalogueDao
            .observeMeals()
            .map<List<CatalogueMealEntity>, LocalResult<List<CatalogueMealCache>>> { meals ->
                LocalResult.Success(meals.map { it.toCache() })
            }.catch { emit(LocalResult.Error(it.toLocalError())) }
            .flowOn(dispatchers.io)
}
