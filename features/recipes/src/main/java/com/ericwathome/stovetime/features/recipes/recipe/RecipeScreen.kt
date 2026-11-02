package com.ericwathome.stovetime.features.recipes.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.data.model.recipes.Ingredient
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.design.components.Pill
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveCheckRow
import com.ericwathome.stovetime.design.components.StoveIconButton
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.recipes.R
import com.ericwathome.stovetime.ui.components.RemotePhoto
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.ViewState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal const val RECIPE_START_COOKING_TAG = "recipe_start_cooking"

private const val PHOTO_ASPECT_RATIO = 360f / 250f

@Composable
fun RecipeRoute(
    recipeId: String,
    onBack: () -> Unit,
    onStartCooking: (recipeId: String, startedAt: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    RecipeScreen(
        onBack = onBack,
        onStartCooking = onStartCooking,
        modifier = modifier,
        viewModel = koinViewModel(key = "recipe_$recipeId") { parametersOf(recipeId) },
    )
}

@Composable
internal fun RecipeScreen(
    onBack: () -> Unit,
    onStartCooking: (recipeId: String, startedAt: Long) -> Unit,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            is RecipeEvent.StartCooking -> onStartCooking(event.recipeId, event.startedAt)
        }
    }

    RecipeContent(state = state, onAction = viewModel::onAction, onBack = onBack, modifier = modifier)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RecipeContent(
    state: RecipeState,
    onAction: (RecipeAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(StovetimeTheme.colors.canvas)
                .semantics { testTagsAsResourceId = true },
    ) {
        when (val recipe = state.recipe) {
            ViewState.Initial, ViewState.Loading -> {
                RecipeMessage(message = stringResource(R.string.recipe_loading))
            }

            is ViewState.Error -> {
                RecipeMessage(
                    message =
                        stringResource(
                            if (recipe.error == DataError.NoConnection || recipe.error == DataError.Timeout) {
                                R.string.recipe_needs_connection
                            } else {
                                R.string.recipe_failed
                            },
                        ),
                    onRetry = { onAction(RecipeAction.OnClickRetry) },
                )
            }

            is ViewState.Success -> {
                RecipeDetails(
                    recipe = recipe.data,
                    xp = state.xp,
                    checkedIngredients = state.checkedIngredients,
                    onAction = onAction,
                )
            }
        }
        StoveIconButton(
            icon = StoveIcons.Close,
            contentDescription = stringResource(R.string.recipe_close),
            onClick = onBack,
            modifier =
                Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(start = StovetimeTheme.spacing.s12, top = StovetimeTheme.spacing.s8),
        )
    }
}

@Composable
private fun RecipeDetails(
    recipe: Recipe,
    xp: Int,
    checkedIngredients: Set<Int>,
    onAction: (RecipeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            RemotePhoto(
                url = recipe.photo,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(PHOTO_ASPECT_RATIO),
            )
            Column(
                modifier = Modifier.padding(start = spacing.s24, end = spacing.s24, top = spacing.s20, bottom = spacing.s16),
                verticalArrangement = Arrangement.spacedBy(spacing.s16),
            ) {
                StoveText(text = recipe.name, style = StovetimeTheme.typography.display)
                RecipePills(recipe = recipe, xp = xp)
                StoveText(text = stringResource(R.string.recipe_you_will_need), style = StovetimeTheme.typography.heading)
                Column {
                    recipe.ingredients.forEachIndexed { index, ingredient ->
                        StoveCheckRow(
                            text = ingredient.label(),
                            checked = index in checkedIngredients,
                            onCheckedChange = { onAction(RecipeAction.OnToggleIngredient(index)) },
                        )
                    }
                }
            }
        }
        StoveButton(
            text = stringResource(R.string.recipe_start_cooking),
            onClick = { onAction(RecipeAction.OnClickStartCooking) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = spacing.s24, end = spacing.s24, top = spacing.s8, bottom = spacing.s16)
                    .testTag(RECIPE_START_COOKING_TAG),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipePills(
    recipe: Recipe,
    xp: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.s8),
        verticalArrangement = Arrangement.spacedBy(spacing.s8),
    ) {
        recipe.category?.let { Pill(text = it) }
        recipe.area?.let { Pill(text = it) }
        Pill(
            text = stringResource(R.string.recipe_xp, xp),
            background = StovetimeTheme.colors.xpSoft,
            color = StovetimeTheme.colors.xpShade,
        )
    }
}

@Composable
private fun Ingredient.label(): String = if (measure.isBlank()) name else stringResource(R.string.recipe_ingredient, measure, name)

@Composable
private fun RecipeMessage(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    val spacing = StovetimeTheme.spacing
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(spacing.s32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.s12, Alignment.CenterVertically),
    ) {
        StoveText(text = message, color = StovetimeTheme.colors.textSecondary, textAlign = TextAlign.Center)
        if (onRetry != null) {
            StoveButton(text = stringResource(R.string.recipe_retry), onClick = onRetry, modifier = Modifier.padding(top = spacing.s8))
        }
    }
}

private val sampleRecipe =
    Recipe(
        id = "52772",
        name = "Teriyaki Chicken Casserole",
        category = "Chicken",
        area = "Japanese",
        photo = null,
        instructions = "Preheat oven to 350° F.",
        ingredients =
            listOf(
                Ingredient(name = "soy sauce", measure = "3/4 cup"),
                Ingredient(name = "water", measure = "1/2 cup"),
                Ingredient(name = "brown sugar", measure = "1/4 cup"),
            ),
    )

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipeContentPreview() {
    StovetimeTheme {
        RecipeContent(
            state = RecipeState(recipe = ViewState.Success(sampleRecipe), checkedIngredients = setOf(1)),
            onAction = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipeLoadingPreview() {
    StovetimeTheme {
        RecipeContent(state = RecipeState(recipe = ViewState.Loading), onAction = {}, onBack = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipeNeedsConnectionPreview() {
    StovetimeTheme {
        RecipeContent(state = RecipeState(recipe = ViewState.Error(DataError.NoConnection)), onAction = {}, onBack = {})
    }
}
