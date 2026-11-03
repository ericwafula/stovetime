package com.ericwathome.stovetime.navigation.entry

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.features.lessons.home.PathRoute
import com.ericwathome.stovetime.navigation.Destination

fun navigationPath(
    key: Destination.Path,
    onOpenLesson: (lessonId: String) -> Unit,
): NavEntry<NavKey> = NavEntry(key) { PathRoute(onOpenLesson = onOpenLesson) }
