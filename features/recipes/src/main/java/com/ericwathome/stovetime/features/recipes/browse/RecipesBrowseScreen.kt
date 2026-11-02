package com.ericwathome.stovetime.features.recipes.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.helpers.Elapsed
import com.ericwathome.stovetime.core.data.model.recipes.Catalogue
import com.ericwathome.stovetime.core.data.model.recipes.CatalogueMeal
import com.ericwathome.stovetime.core.data.model.recipes.RecipeSyncStatus
import com.ericwathome.stovetime.design.components.Pill
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveChip
import com.ericwathome.stovetime.design.components.StoveNavBarClearance
import com.ericwathome.stovetime.design.components.StovePhotoCard
import com.ericwathome.stovetime.design.components.StoveSearchField
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.recipes.R
import com.ericwathome.stovetime.ui.components.RemotePhoto
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.agoText
import com.ericwathome.stovetime.ui.helpers.isKeyboardOpen
import org.koin.compose.viewmodel.koinViewModel

internal const val RECIPES_SEARCH_TAG = "recipes_search"
internal const val RECIPES_FIRST_CARD_TAG = "recipes_first_card"

@Composable
fun RecipesBrowseRoute(
    onOpenRecipe: (recipeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    RecipesBrowseScreen(onOpenRecipe = onOpenRecipe, modifier = modifier)
}

@Composable
internal fun RecipesBrowseScreen(
    onOpenRecipe: (recipeId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipesBrowseViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            is RecipesBrowseEvent.OpenRecipe -> onOpenRecipe(event.recipeId)
        }
    }

    RecipesBrowseContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun RecipesBrowseContent(
    state: RecipesBrowseState,
    onAction: (RecipesBrowseAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val pullState = rememberPullToRefreshState()
    val keyboardOpen = isKeyboardOpen
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(StovetimeTheme.colors.canvas)
                .semantics { testTagsAsResourceId = true }
                .windowInsetsPadding(WindowInsets.statusBars)
                .pullToRefresh(
                    isRefreshing = state.isRefreshing,
                    state = pullState,
                    onRefresh = { onAction(RecipesBrowseAction.OnRefresh) },
                ),
    ) {
        Column(
            modifier = Modifier.padding(start = spacing.s24, end = spacing.s24, top = spacing.s8),
            verticalArrangement = Arrangement.spacedBy(spacing.s20),
        ) {
            if (!keyboardOpen) {
                BrowseHeader(state = state)
            }
            StoveSearchField(
                value = state.query,
                onValueChange = { onAction(RecipesBrowseAction.OnChangeQuery(it)) },
                placeholder = stringResource(R.string.recipes_search_placeholder),
                modifier = Modifier.testTag(RECIPES_SEARCH_TAG),
            )
        }
        CategoryRow(
            categories = state.categories,
            selected = state.selectedCategory,
            onSelect = { onAction(RecipesBrowseAction.OnSelectCategory(it)) },
            modifier = Modifier.padding(top = spacing.s8),
        )
        PullIndicator(isRefreshing = state.isRefreshing, distanceFraction = pullState.distanceFraction)
        when (val content = state.content) {
            BrowseContent.Loading -> {
                BrowseMessage(message = stringResource(R.string.recipes_loading))
            }

            is BrowseContent.Empty -> {
                BrowseMessage(
                    title = stringResource(R.string.recipes_empty_title),
                    message = stringResource(if (content.isOffline) R.string.recipes_empty_offline else R.string.recipes_empty_failed),
                    onRetry = { onAction(RecipesBrowseAction.OnClickRetry) },
                )
            }

            is BrowseContent.NoMatches -> {
                BrowseMessage(
                    message =
                        if (content.query.isEmpty()) {
                            stringResource(R.string.recipes_category_empty)
                        } else {
                            stringResource(R.string.recipes_no_matches, content.query)
                        },
                )
            }

            is BrowseContent.Meals -> {
                MealGrid(meals = content.meals, onClickMeal = { onAction(RecipesBrowseAction.OnClickRecipe(it)) })
            }
        }
    }
}

@Composable
private fun BrowseHeader(
    state: RecipesBrowseState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        StoveText(text = stringResource(R.string.recipes_title), style = StovetimeTheme.typography.title)
        StoveText(
            text = state.lastSynced?.let { stringResource(R.string.recipes_synced, it.agoText()) } ?: stringResource(R.string.recipes_not_synced),
            style = StovetimeTheme.typography.caption,
            color = StovetimeTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun CategoryRow(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = spacing.s24),
        horizontalArrangement = Arrangement.spacedBy(spacing.s8),
    ) {
        item(key = "all") {
            StoveChip(text = stringResource(R.string.recipes_category_all), selected = selected == null, onClick = { onSelect(null) })
        }
        items(categories, key = { it }) { category ->
            StoveChip(text = category, selected = selected == category, onClick = { onSelect(category) })
        }
    }
}

@Composable
private fun PullIndicator(
    isRefreshing: Boolean,
    distanceFraction: Float,
    modifier: Modifier = Modifier,
) {
    if (!isRefreshing && distanceFraction <= 0f) return
    Box(modifier = modifier.fillMaxWidth().padding(top = StovetimeTheme.spacing.s8), contentAlignment = Alignment.Center) {
        Pill(
            text = stringResource(if (isRefreshing) R.string.recipes_syncing else R.string.recipes_pull_to_sync),
            modifier = Modifier.graphicsLayer { alpha = if (isRefreshing) 1f else distanceFraction.coerceIn(0f, 1f) },
        )
    }
}

@Composable
private fun MealGrid(
    meals: List<CatalogueMeal>,
    onClickMeal: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val focusManager = LocalFocusManager.current
    val navigationBars = WindowInsets.navigationBars.asPaddingValues()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = spacing.s24,
                end = spacing.s24,
                top = spacing.s12,
                bottom = StoveNavBarClearance + navigationBars.calculateBottomPadding(),
            ),
        horizontalArrangement = Arrangement.spacedBy(spacing.s16),
        verticalArrangement = Arrangement.spacedBy(spacing.s16),
    ) {
        itemsIndexed(meals, key = { _, meal -> meal.id }) { index, meal ->
            StovePhotoCard(
                title = meal.name,
                caption = meal.category,
                onClick = {
                    focusManager.clearFocus()
                    onClickMeal(meal.id)
                },
                modifier = if (index == 0) Modifier.testTag(RECIPES_FIRST_CARD_TAG) else Modifier,
            ) {
                RemotePhoto(url = meal.photo, contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun BrowseMessage(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    val spacing = StovetimeTheme.spacing
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.s32, vertical = spacing.s32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.s12),
    ) {
        if (title != null) {
            StoveText(text = title, style = StovetimeTheme.typography.heading, textAlign = TextAlign.Center)
        }
        StoveText(text = message, color = StovetimeTheme.colors.textSecondary, textAlign = TextAlign.Center)
        if (onRetry != null) {
            StoveButton(text = stringResource(R.string.recipes_retry), onClick = onRetry, modifier = Modifier.padding(top = spacing.s8))
        }
    }
}

private val sampleMeals =
    listOf(
        CatalogueMeal(id = "52795", name = "Chicken Handi", photo = null, category = "Chicken"),
        CatalogueMeal(id = "52803", name = "Beef and Mustard Pie", photo = null, category = "Beef"),
        CatalogueMeal(id = "52887", name = "Kedgeree", photo = null, category = "Seafood"),
        CatalogueMeal(id = "52770", name = "Spaghetti Bolognese", photo = null, category = "Pasta"),
    )

private val sampleState =
    RecipesBrowseState(
        catalogue =
            ViewState.Success(
                Catalogue(categories = listOf("Chicken", "Beef", "Pasta", "Seafood"), meals = sampleMeals, storedCount = 4),
            ),
        syncStatus = RecipeSyncStatus(lastSyncedAt = 1L),
        lastSynced = Elapsed.Hours(2),
    )

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipesBrowseContentPreview() {
    StovetimeTheme {
        RecipesBrowseContent(state = sampleState, onAction = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipesBrowseLoadingPreview() {
    StovetimeTheme {
        RecipesBrowseContent(state = RecipesBrowseState(), onAction = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipesBrowseEmptyOfflinePreview() {
    StovetimeTheme {
        RecipesBrowseContent(
            state =
                RecipesBrowseState(
                    catalogue = ViewState.Success(Catalogue(categories = emptyList(), meals = emptyList(), storedCount = 0)),
                    isOnline = false,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipesBrowseNoMatchesPreview() {
    StovetimeTheme {
        RecipesBrowseContent(
            state =
                sampleState.copy(
                    query = "xyz",
                    catalogue = ViewState.Success(Catalogue(categories = listOf("Chicken"), meals = emptyList(), storedCount = 4)),
                ),
            onAction = {},
        )
    }
}
