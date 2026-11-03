package com.ericwathome.stovetime.datasources.local.sources.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.PreferenceStore
import com.ericwathome.stovetime.datasources.local.helpers.preference

interface WeeklyReminderLocalDatasource {
    val lastSentWeekStart: PreferenceStore<Long>
}

internal class WeeklyReminderLocalDatasourceImpl(
    dataStore: DataStore<Preferences>,
    dispatchers: StovetimeDispatchers,
) : WeeklyReminderLocalDatasource {
    override val lastSentWeekStart = dataStore.preference(longPreferencesKey("last_sent_week_start"), dispatchers)
}
