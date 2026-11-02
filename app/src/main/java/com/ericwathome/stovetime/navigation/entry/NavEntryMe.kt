package com.ericwathome.stovetime.navigation.entry

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.features.me.home.MeRoute
import com.ericwathome.stovetime.navigation.Destination

fun navigationMe(
    key: Destination.Me,
    onOpenLesson: (lessonId: String) -> Unit,
    onOpenRecipe: (recipeId: String) -> Unit,
    onOpenPath: () -> Unit,
): NavEntry<NavKey> =
    NavEntry(key) {
        MeRoute(
            onOpenLesson = onOpenLesson,
            onOpenRecipe = onOpenRecipe,
            onOpenPath = onOpenPath,
        )
    }
