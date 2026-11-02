package com.ericwathome.stovetime.datasources.local.sources.recipes.recipe

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.RecipeDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.mappers.toEntity
import com.ericwathome.stovetime.datasources.local.models.RecipeCache

fun interface SaveRecipeLocalDatasource {
    suspend operator fun invoke(recipe: RecipeCache): LocalResult<Boolean>
}

internal class SaveRecipeLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val recipeDao: RecipeDao,
) : SaveRecipeLocalDatasource {
    override suspend fun invoke(recipe: RecipeCache): LocalResult<Boolean> =
        safeDatabaseCall(dispatchers.io) {
            recipeDao.upsertRecipe(recipe.toEntity())
            true
        }
}
