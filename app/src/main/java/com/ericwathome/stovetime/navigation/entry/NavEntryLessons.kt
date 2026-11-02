package com.ericwathome.stovetime.navigation.entry

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.features.lessons.cook.CookRoute
import com.ericwathome.stovetime.features.lessons.finish.FinishRoute
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

fun navigationCook(
    key: Destination.Cook,
    onClose: () -> Unit,
    onFinish: (kind: CookKind, dishId: String, startedAt: Long, finishedAt: Long) -> Unit,
): NavEntry<NavKey> =
    NavEntry(key) {
        CookRoute(
            kind = CookKind.entries.firstOrNull { it.name == key.kind } ?: CookKind.Lesson,
            dishId = key.dishId,
            startedAt = key.startedAt,
            step = key.step,
            onClose = onClose,
            onFinish = onFinish,
        )
    }

fun navigationFinish(
    key: Destination.CookDone,
    onContinue: () -> Unit,
): NavEntry<NavKey> =
    NavEntry(key) {
        FinishRoute(
            kind = CookKind.entries.firstOrNull { it.name == key.kind } ?: CookKind.Lesson,
            dishId = key.dishId,
            startedAt = key.startedAt,
            finishedAt = key.finishedAt,
            onContinue = onContinue,
        )
    }
