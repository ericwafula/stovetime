package com.ericwathome.stovetime.datasources.local.sources.recipes.recipe

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.RecipeDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalError
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.RecipeCache

fun interface SaveRecipeLocalDatasource {
    suspend operator fun invoke(recipe: RecipeCache): LocalResult<Boolean>
}

internal class SaveRecipeLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val recipeDao: RecipeDao,
) : SaveRecipeLocalDatasource {
    // TODO(workshop 6.2): upsert the recipe into recipeDao as an entity, wrapped in safeDatabaseCall on the io dispatcher, and return true
    override suspend fun invoke(recipe: RecipeCache): LocalResult<Boolean> = LocalResult.Error(LocalError.Unknown("Built live in the workshop"))
}
