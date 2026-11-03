package com.ericwathome.stovetime.core.data.model.recipes

import kotlin.time.Duration.Companion.days

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

private val SYNC_DUE_AFTER = 1.days
private val SYNC_STALE_AFTER = 7.days

fun RecipeSyncStatus.isSyncDue(nowMillis: Long): Boolean =
    lastSyncedAt == null || nowMillis - lastSyncedAt > SYNC_DUE_AFTER.inWholeMilliseconds

fun RecipeSyncStatus.keepsFailing(nowMillis: Long): Boolean =
    lastSyncedAt != null &&
        lastFailedAt != null &&
        lastFailedAt > lastSyncedAt &&
        nowMillis - lastSyncedAt > SYNC_STALE_AFTER.inWholeMilliseconds
