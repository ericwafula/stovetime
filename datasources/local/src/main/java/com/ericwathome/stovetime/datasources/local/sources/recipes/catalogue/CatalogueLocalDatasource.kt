package com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue

interface CatalogueLocalDatasource {
    val observeCategories: ObserveCategoriesLocalDatasource
    val observeMeals: ObserveCatalogueMealsLocalDatasource
    val getMeals: GetCatalogueMealsLocalDatasource
    val replaceCatalogue: ReplaceCatalogueLocalDatasource
}

internal class CatalogueLocalDatasourceImpl(
    override val observeCategories: ObserveCategoriesLocalDatasource,
    override val observeMeals: ObserveCatalogueMealsLocalDatasource,
    override val getMeals: GetCatalogueMealsLocalDatasource,
    override val replaceCatalogue: ReplaceCatalogueLocalDatasource,
) : CatalogueLocalDatasource
