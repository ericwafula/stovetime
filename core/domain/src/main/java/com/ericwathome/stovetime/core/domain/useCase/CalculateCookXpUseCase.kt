package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.domain.rules.RECIPE_XP
import com.ericwathome.stovetime.core.domain.rules.lessonXp

fun interface CalculateCookXpUseCase {
    operator fun invoke(
        kind: CookKind,
        lessonXp: Int,
        cookedBefore: Boolean,
    ): Int
}

internal class CalculateCookXpUseCaseImpl : CalculateCookXpUseCase {
    override fun invoke(
        kind: CookKind,
        lessonXp: Int,
        cookedBefore: Boolean,
    ): Int =
        when (kind) {
            CookKind.Lesson -> lessonXp(lessonXp = lessonXp, cookedBefore = cookedBefore)
            CookKind.Recipe -> RECIPE_XP
        }
}
