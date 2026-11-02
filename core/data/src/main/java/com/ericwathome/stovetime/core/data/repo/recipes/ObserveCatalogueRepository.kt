package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.common.helpers.matchesSearch
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.helpers.toDataError
import com.ericwathome.stovetime.core.data.mappers.recipes.toData
import com.ericwathome.stovetime.core.data.model.recipes.Catalogue
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.CatalogueLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun interface ObserveCatalogueRepository {
    operator fun invoke(
        category: String?,
        query: String,
    ): Flow<DataResult<Catalogue>>
}

internal class ObserveCatalogueRepositoryImpl(
    private val catalogueLocalDatasource: CatalogueLocalDatasource,
) : ObserveCatalogueRepository {
    override fun invoke(
        category: String?,
        query: String,
    ): Flow<DataResult<Catalogue>> =
        combine(
            catalogueLocalDatasource.observeCategories(),
            catalogueLocalDatasource.observeMeals(),
        ) { categoriesResult, mealsResult ->
            val categories =
                when (categoriesResult) {
                    is LocalResult.Success -> categoriesResult.data.map { it.name }
                    is LocalResult.Error -> return@combine DataResult.Error(categoriesResult.error.toDataError())
                }
            mealsResult.mapToData { meals ->
                Catalogue(
                    categories = categories,
                    meals =
                        meals
                            .filter { category == null || it.category == category }
                            .filter { it.name.matchesSearch(query) }
                            .map { it.toData() },
                    storedCount = meals.size,
                )
            }
        }
}
