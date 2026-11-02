package com.ericwathome.stovetime.datasources.local.sources.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.PreferenceStore
import com.ericwathome.stovetime.datasources.local.helpers.preference

interface TimerPromptsLocalDatasource {
    val notificationsExplained: PreferenceStore<Boolean>
    val lateAlarmsExplained: PreferenceStore<Boolean>
}

internal class TimerPromptsLocalDatasourceImpl(
    dataStore: DataStore<Preferences>,
    dispatchers: StovetimeDispatchers,
) : TimerPromptsLocalDatasource {
    override val notificationsExplained = dataStore.preference(booleanPreferencesKey("notifications_explained"), dispatchers)
    override val lateAlarmsExplained = dataStore.preference(booleanPreferencesKey("late_alarms_explained"), dispatchers)
}
