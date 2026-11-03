package com.ericwathome.stovetime.datasources.local.sources.recipes.recipe

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.RecipeDao
import com.ericwathome.stovetime.datasources.local.helpers.LocalError
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.RecipeCache

fun interface GetRecipeLocalDatasource {
    suspend operator fun invoke(id: String): LocalResult<RecipeCache?>
}

internal class GetRecipeLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val recipeDao: RecipeDao,
) : GetRecipeLocalDatasource {
    // TODO(workshop 6.2): read the recipe from recipeDao and map it to a RecipeCache, wrapped in safeDatabaseCall on the io dispatcher; null when it isn't on the phone yet
    override suspend fun invoke(id: String): LocalResult<RecipeCache?> = LocalResult.Error(LocalError.Unknown("Built live in the workshop"))
}
