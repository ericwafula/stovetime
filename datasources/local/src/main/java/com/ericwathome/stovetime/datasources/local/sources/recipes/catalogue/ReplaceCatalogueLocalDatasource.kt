package com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CatalogueDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.mappers.toEntity
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CategoryCache

fun interface ReplaceCatalogueLocalDatasource {
    suspend operator fun invoke(
        categories: List<CategoryCache>,
        upserts: List<CatalogueMealCache>,
        deleteIds: List<String>,
    ): LocalResult<Boolean>
}

internal class ReplaceCatalogueLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val catalogueDao: CatalogueDao,
) : ReplaceCatalogueLocalDatasource {
    override suspend fun invoke(
        categories: List<CategoryCache>,
        upserts: List<CatalogueMealCache>,
        deleteIds: List<String>,
    ): LocalResult<Boolean> =
        safeDatabaseCall(dispatchers.io) {
            catalogueDao.replaceCatalogue(
                categories = categories.map { it.toEntity() },
                upserts = upserts.map { it.toEntity() },
                deleteIds = deleteIds,
            )
            true
        }
}
