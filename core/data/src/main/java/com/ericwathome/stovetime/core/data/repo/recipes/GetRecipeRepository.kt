package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.RecipeLocalDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.RecipesRemoteDatasource

fun interface GetRecipeRepository {
    suspend operator fun invoke(id: String): DataResult<Recipe>
}

internal class GetRecipeRepositoryImpl(
    private val recipesRemoteDatasource: RecipesRemoteDatasource,
    private val recipeLocalDatasource: RecipeLocalDatasource,
) : GetRecipeRepository {
    // TODO(workshop 6.3): look in the phone's copy first and return it if it's there; otherwise fetch the meal from TheMealDB, save it to the phone (stamped with Now) and return it. No meal for the id is an Unknown error
    override suspend fun invoke(id: String): DataResult<Recipe> = DataResult.Error(DataError.Unknown("Built live in the workshop"))
}
