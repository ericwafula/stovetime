package com.ericwathome.stovetime.datasources.local.sources.recipes.recipe

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.RecipeDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.mappers.toCache
import com.ericwathome.stovetime.datasources.local.models.RecipeCache

fun interface GetRecipeLocalDatasource {
    suspend operator fun invoke(id: String): LocalResult<RecipeCache?>
}

internal class GetRecipeLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val recipeDao: RecipeDao,
) : GetRecipeLocalDatasource {
    override suspend fun invoke(id: String): LocalResult<RecipeCache?> =
        safeDatabaseCall(dispatchers.io) {
            recipeDao.getRecipe(id)?.toCache()
        }
}
