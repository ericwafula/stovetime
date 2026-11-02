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
}
