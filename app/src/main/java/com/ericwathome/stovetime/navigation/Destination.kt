package com.ericwathome.stovetime.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Path : Destination

    @Serializable
    data object Recipes : Destination

    @Serializable
    data object Timers : Destination

    @Serializable
    data object Me : Destination

    @Serializable
    data class LessonIntro(
        val lessonId: String,
    ) : Destination

    @Serializable
    data class Cook(
        val kind: String,
        val dishId: String,
        val startedAt: Long,
        val step: Int = 0,
    ) : Destination

    @Serializable
    data class CookDone(
        val kind: String,
        val dishId: String,
        val startedAt: Long,
        val finishedAt: Long,
    ) : Destination
}
