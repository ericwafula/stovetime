package com.ericwathome.stovetime.features.recipes.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ericwathome.stovetime.design.components.StoveIconButton
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.recipes.R
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

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

// TODO(workshop 6.5): build the recipe page from the Figma frame Recipe — Intro (72:120) with the implement-design and new-compose-screen skills: collect the ViewModel's state and StartCooking event, then a stateless RecipeContent with the photo, name, category, area and XP pills, the ingredient checklist and Start cooking, plus loading and error states with Try again
@Composable
internal fun RecipeScreen(
    onBack: () -> Unit,
    onStartCooking: (recipeId: String, startedAt: Long) -> Unit,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier,
) {
    RecipePlaceholder(onBack = onBack, modifier = modifier)
}

@Composable
private fun RecipePlaceholder(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(StovetimeTheme.colors.canvas),
    ) {
        StoveIconButton(
            icon = StoveIcons.Close,
            contentDescription = stringResource(R.string.recipe_close),
            onClick = onBack,
            modifier =
                Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(StovetimeTheme.spacing.s16),
        )
        StoveText(
            text = stringResource(R.string.recipe_built_live),
            style = StovetimeTheme.typography.body,
            color = StovetimeTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(StovetimeTheme.spacing.s32),
        )
    }
}

@PreviewLightDark
@Composable
private fun RecipePlaceholderPreview() {
    StovetimePreviewTheme {
        RecipePlaceholder(onBack = {})
    }
}
