package com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CatalogueDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.mappers.toCache
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache

fun interface GetCatalogueMealsLocalDatasource {
    suspend operator fun invoke(): LocalResult<List<CatalogueMealCache>>
}

internal class GetCatalogueMealsLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val catalogueDao: CatalogueDao,
) : GetCatalogueMealsLocalDatasource {
    override suspend fun invoke(): LocalResult<List<CatalogueMealCache>> =
        safeDatabaseCall(dispatchers.io) {
            catalogueDao.getMeals().map { it.toCache() }
        }
}
