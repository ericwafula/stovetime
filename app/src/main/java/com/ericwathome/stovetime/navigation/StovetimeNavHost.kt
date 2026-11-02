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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.notifications.toDestination
import com.ericwathome.stovetime.design.components.StoveNavBar
import com.ericwathome.stovetime.design.components.StoveNavItem
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.navigation.entry.navigationCook
import com.ericwathome.stovetime.navigation.entry.navigationFinish
import com.ericwathome.stovetime.navigation.entry.navigationLessonIntro
import com.ericwathome.stovetime.navigation.entry.navigationPath
import com.ericwathome.stovetime.navigation.entry.navigationPlaceholder
import com.ericwathome.stovetime.navigation.entry.navigationRecipe
import com.ericwathome.stovetime.navigation.entry.navigationRecipesBrowse
import com.ericwathome.stovetime.ui.helpers.isKeyboardOpen

private const val CROSSFADE_MILLIS = 180

@Composable
fun StovetimeNavHost(
    timerPayload: String?,
    onTimerPayloadOpened: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Destination.Path)
    val navigator = remember(backStack) { Navigator(backStack, TopLevelTab.destinations) }
    LaunchedEffect(timerPayload) {
        timerPayload ?: return@LaunchedEffect
        navigator.open(TimerTarget.decode(timerPayload).toDestination())
        onTimerPayloadOpened()
    }
    val crossfade = fadeIn(tween(CROSSFADE_MILLIS)) togetherWith fadeOut(tween(CROSSFADE_MILLIS))

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(StovetimeTheme.colors.canvas)
                .semantics { testTagsAsResourceId = true },
    ) {
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
                    Destination.Path ->
                        navigationPath(
                            Destination.Path,
                            onOpenLesson = { lessonId -> navigator.navigate(Destination.LessonIntro(lessonId)) },
                        )
                    Destination.Recipes ->
                        navigationRecipesBrowse(
                            Destination.Recipes,
                            onOpenRecipe = { recipeId -> navigator.navigate(Destination.Recipe(recipeId)) },
                        )
                    Destination.Timers -> navigationPlaceholder(destination, R.string.tab_timers)
                    Destination.Me -> navigationPlaceholder(destination, R.string.tab_me)
                    is Destination.LessonIntro ->
                        navigationLessonIntro(
                            destination,
                            onClose = navigator::backward,
                            onOpenCook = { lessonId, startedAt ->
                                navigator.replace(Destination.Cook(kind = CookKind.Lesson.name, dishId = lessonId, startedAt = startedAt))
                            },
                        )
                    is Destination.Cook ->
                        navigationCook(
                            destination,
                            onClose = navigator::backward,
                            onFinish = { kind, dishId, startedAt, finishedAt ->
                                navigator.replace(Destination.CookDone(kind.name, dishId, startedAt, finishedAt))
                            },
                        )
                    is Destination.CookDone -> navigationFinish(destination, onContinue = navigator::backward)
                    is Destination.Recipe ->
                        navigationRecipe(
                            destination,
                            onBack = navigator::backward,
                            onStartCooking = { recipeId ->
                                navigator.navigate(
                                    Destination.Cook(kind = CookKind.Recipe.name, dishId = recipeId, startedAt = System.currentTimeMillis()),
                                )
                            },
                        )
                }
            },
        )
        if (navigator.isOnTab && !isKeyboardOpen) {
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
