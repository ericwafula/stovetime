package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.data.model.recipes.RecipeSyncStatus
import com.ericwathome.stovetime.core.data.workers.recipes.ObserveRecipeSyncRunning
import com.ericwathome.stovetime.datasources.local.sources.settings.RecipeSyncLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

fun interface ObserveRecipeSyncStatusRepository {
    operator fun invoke(): Flow<RecipeSyncStatus>
}

internal class ObserveRecipeSyncStatusRepositoryImpl(
    private val recipeSyncLocalDatasource: RecipeSyncLocalDatasource,
    private val observeRecipeSyncRunning: ObserveRecipeSyncRunning,
) : ObserveRecipeSyncStatusRepository {
    override fun invoke(): Flow<RecipeSyncStatus> =
        combine(
            recipeSyncLocalDatasource.lastSyncedAt.observe(),
            recipeSyncLocalDatasource.lastFailedAt.observe(),
            observeRecipeSyncRunning(),
        ) { lastSyncedAt, lastFailedAt, isSyncing ->
            RecipeSyncStatus(lastSyncedAt = lastSyncedAt, lastFailedAt = lastFailedAt, isSyncing = isSyncing)
        }.distinctUntilChanged()
}
