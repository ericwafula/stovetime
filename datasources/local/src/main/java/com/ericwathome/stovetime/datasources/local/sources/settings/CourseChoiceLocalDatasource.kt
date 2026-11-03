package com.ericwathome.stovetime.datasources.local.sources.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.PreferenceStore
import com.ericwathome.stovetime.datasources.local.helpers.preference

interface CourseChoiceLocalDatasource {
    val courseId: PreferenceStore<String>
}

internal class CourseChoiceLocalDatasourceImpl(
    dataStore: DataStore<Preferences>,
    dispatchers: StovetimeDispatchers,
) : CourseChoiceLocalDatasource {
    override val courseId = dataStore.preference(stringPreferencesKey("course_id"), dispatchers)
}
