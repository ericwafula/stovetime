package com.ericwathome.stovetime.datasources.local.sources.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.PreferenceStore
import com.ericwathome.stovetime.datasources.local.helpers.preference

interface RecipeSyncLocalDatasource {
    val lastSyncedAt: PreferenceStore<Long>
    val lastFailedAt: PreferenceStore<Long>
}

internal class RecipeSyncLocalDatasourceImpl(
    dataStore: DataStore<Preferences>,
    dispatchers: StovetimeDispatchers,
) : RecipeSyncLocalDatasource {
    override val lastSyncedAt = dataStore.preference(longPreferencesKey("last_synced_at"), dispatchers)
    override val lastFailedAt = dataStore.preference(longPreferencesKey("last_failed_at"), dispatchers)
}
