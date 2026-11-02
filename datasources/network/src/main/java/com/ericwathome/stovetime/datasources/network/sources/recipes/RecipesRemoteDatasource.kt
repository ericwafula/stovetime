package com.ericwathome.stovetime.datasources.network.sources.recipes

interface RecipesRemoteDatasource {
    val getCategories: GetCategoriesRemoteDatasource
    val getMealsByCategory: GetMealsByCategoryRemoteDatasource
    val searchMeals: SearchMealsRemoteDatasource
    val lookupMeal: LookupMealRemoteDatasource
}

internal class RecipesRemoteDatasourceImpl(
    override val getCategories: GetCategoriesRemoteDatasource,
    override val getMealsByCategory: GetMealsByCategoryRemoteDatasource,
    override val searchMeals: SearchMealsRemoteDatasource,
    override val lookupMeal: LookupMealRemoteDatasource,
) : RecipesRemoteDatasource
