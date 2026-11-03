package com.ericwathome.stovetime.navigation

import androidx.annotation.StringRes
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.design.components.StoveIcons

enum class TopLevelTab(
    val destination: Destination,
    val icon: StoveIcons,
    @param:StringRes val label: Int,
) {
    Path(Destination.Path, StoveIcons.Home, R.string.tab_path),
    Recipes(Destination.Recipes, StoveIcons.Book, R.string.tab_recipes),
    Timers(Destination.Timers, StoveIcons.Timer, R.string.tab_timers),
    Me(Destination.Me, StoveIcons.User, R.string.tab_me),
    ;

    companion object {
        val destinations: List<Destination> = entries.map { it.destination }
    }
}
