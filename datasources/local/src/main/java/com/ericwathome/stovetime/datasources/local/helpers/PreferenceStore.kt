package com.ericwathome.stovetime.datasources.local.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

interface PreferenceStore<T> {
    suspend fun save(value: T): LocalResult<Boolean>

    suspend fun get(): LocalResult<T?>

    fun observe(): Flow<T?>

    suspend fun delete(): LocalResult<Boolean>
}

internal class DataStorePreferenceStore<T>(
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val dispatchers: StovetimeDispatchers,
) : PreferenceStore<T> {
    override suspend fun save(value: T): LocalResult<Boolean> =
        safeDatabaseCall(dispatchers.io) {
            dataStore.edit { it[key] = value }
            true
        }

    override suspend fun get(): LocalResult<T?> = safeDatabaseCall(dispatchers.io) { observe().first() }

    override fun observe(): Flow<T?> =
        dataStore.data
            .catch { error ->
                if (error !is IOException) throw error
                Timber.e(error)
                emit(emptyPreferences())
            }.map { preferences -> preferences[key] }
            .distinctUntilChanged()

    override suspend fun delete(): LocalResult<Boolean> =
        safeDatabaseCall(dispatchers.io) {
            dataStore.edit { it.remove(key) }
            true
        }
}

internal fun <T> DataStore<Preferences>.preference(
    key: Preferences.Key<T>,
    dispatchers: StovetimeDispatchers,
): PreferenceStore<T> = DataStorePreferenceStore(this, key, dispatchers)
