package com.ericwathome.stovetime.datasources.local.sources.recipes.recipe

interface RecipeLocalDatasource {
    val getRecipe: GetRecipeLocalDatasource
    val saveRecipe: SaveRecipeLocalDatasource
}

internal class RecipeLocalDatasourceImpl(
    override val getRecipe: GetRecipeLocalDatasource,
    override val saveRecipe: SaveRecipeLocalDatasource,
) : RecipeLocalDatasource
