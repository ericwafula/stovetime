package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.common.result.getOrNull
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.mappers.recipes.toData
import com.ericwathome.stovetime.core.data.mappers.recipes.toRecipeCache
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
    override suspend fun invoke(id: String): DataResult<Recipe> {
        recipeLocalDatasource.getRecipe(id).mapToData { it?.toData() }.getOrNull()?.let { return DataResult.Success(it) }

        val fetched =
            when (val result = recipesRemoteDatasource.lookupMeal(id).mapToData { it.meals?.firstOrNull() }) {
                is DataResult.Success -> result.data ?: return DataResult.Error(DataError.Unknown("No recipe with id $id"))
                is DataResult.Error -> return result
            }
        val recipe = fetched.toRecipeCache(fetchedAt = Now.toEpochMilliseconds())
        recipeLocalDatasource.saveRecipe(recipe)
        return DataResult.Success(recipe.toData())
    }
}
