package com.ericwathome.stovetime.core.data.repo.recipes

interface RecipesRepository {
    val syncCatalogue: SyncCatalogueRepository
    val observeCatalogue: ObserveCatalogueRepository
    val observeSyncStatus: ObserveRecipeSyncStatusRepository
    val getRecipe: GetRecipeRepository
}

internal class RecipesRepositoryImpl(
    override val syncCatalogue: SyncCatalogueRepository,
    override val observeCatalogue: ObserveCatalogueRepository,
    override val observeSyncStatus: ObserveRecipeSyncStatusRepository,
    override val getRecipe: GetRecipeRepository,
) : RecipesRepository
