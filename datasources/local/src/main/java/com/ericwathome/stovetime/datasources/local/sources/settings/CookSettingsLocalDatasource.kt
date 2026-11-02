package com.ericwathome.stovetime.datasources.local.sources.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.PreferenceStore
import com.ericwathome.stovetime.datasources.local.helpers.preference

interface CookSettingsLocalDatasource {
    val weeklyGoal: PreferenceStore<Int>
    val keepScreenOn: PreferenceStore<Boolean>
}

internal class CookSettingsLocalDatasourceImpl(
    dataStore: DataStore<Preferences>,
    dispatchers: StovetimeDispatchers,
) : CookSettingsLocalDatasource {
    override val weeklyGoal = dataStore.preference(intPreferencesKey("weekly_goal"), dispatchers)
    override val keepScreenOn = dataStore.preference(booleanPreferencesKey("keep_screen_on"), dispatchers)
}
