package com.ericwathome.stovetime.datasources.local.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import java.io.File

internal enum class PreferencesFile(
    val fileName: String,
) {
    CookSettings("cook_settings"),
    TimerPrompts("timer_prompts"),
    RecipeSync("recipe_sync"),
    WeeklyReminder("weekly_reminder"),
    CourseChoice("course_choice"),
}

internal fun createPreferencesDataStore(
    scope: CoroutineScope,
    produceFile: () -> File,
): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        scope = scope,
        produceFile = produceFile,
    )
