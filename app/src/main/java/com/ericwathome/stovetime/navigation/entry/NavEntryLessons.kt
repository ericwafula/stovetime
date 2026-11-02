package com.ericwathome.stovetime.navigation.entry

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.features.lessons.intro.LessonIntroRoute
import com.ericwathome.stovetime.navigation.Destination

fun navigationLessonIntro(
    key: Destination.LessonIntro,
    onClose: () -> Unit,
    onOpenCook: (lessonId: String, startedAt: Long) -> Unit,
): NavEntry<NavKey> =
    NavEntry(key) {
        LessonIntroRoute(lessonId = key.lessonId, onClose = onClose, onOpenCook = onOpenCook)
    }
