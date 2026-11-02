package com.ericwathome.stovetime.core.data.model.recipes

data class CatalogueMeal(
    val id: String,
    val name: String,
    val photo: String?,
    val category: String,
)

data class Catalogue(
    val categories: List<String>,
    val meals: List<CatalogueMeal>,
    val storedCount: Int,
)

data class RecipeSyncStatus(
    val lastSyncedAt: Long? = null,
    val lastFailedAt: Long? = null,
    val isSyncing: Boolean = false,
)
