package com.ericwathome.stovetime.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.design.components.StoveNavBar
import com.ericwathome.stovetime.design.components.StoveNavItem
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.navigation.entry.navigationPlaceholder

private const val CROSSFADE_MILLIS = 180

@Composable
fun StovetimeNavHost(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Destination.Path)
    val navigator = remember(backStack) { Navigator(backStack, TopLevelTab.destinations) }
    val crossfade = fadeIn(tween(CROSSFADE_MILLIS)) togetherWith fadeOut(tween(CROSSFADE_MILLIS))

    Box(modifier = modifier.fillMaxSize().background(StovetimeTheme.colors.canvas)) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            onBack = navigator::backward,
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            transitionSpec = { crossfade },
            popTransitionSpec = { crossfade },
            predictivePopTransitionSpec = { crossfade },
            entryProvider = { key ->
                when (val destination = key as Destination) {
                    Destination.Path -> navigationPlaceholder(destination, R.string.tab_path)
                    Destination.Recipes -> navigationPlaceholder(destination, R.string.tab_recipes)
                    Destination.Timers -> navigationPlaceholder(destination, R.string.tab_timers)
                    Destination.Me -> navigationPlaceholder(destination, R.string.tab_me)
                }
            },
        )
        if (navigator.isOnTab) {
            StoveNavBar(
                items = TopLevelTab.entries.map { StoveNavItem(icon = it.icon, label = stringResource(it.label)) },
                selected = TopLevelTab.entries.indexOfFirst { it.destination == navigator.currentTab },
                onSelect = { index -> navigator.selectTab(TopLevelTab.entries[index].destination) },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = StovetimeTheme.spacing.s16, vertical = StovetimeTheme.spacing.s12),
            )
        }
    }
}
