package com.ericwathome.stovetime.features.recipes.recipe

import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState

sealed interface RecipeAction {
    data object OnClickRetry : RecipeAction

    data object OnClickStartCooking : RecipeAction

    data class OnToggleIngredient(
        val index: Int,
    ) : RecipeAction
}

sealed interface RecipeEvent {
    data class StartCooking(
        val recipeId: String,
        val startedAt: Long,
    ) : RecipeEvent
}

data class RecipeState(
    val recipe: ViewState<Recipe> = ViewState.Initial,
    val checkedIngredients: Set<Int> = emptySet(),
    val xp: Int = 0,
) {
    val isStartEnabled: Boolean
        get() = recipe is ViewState.Success
}

class RecipeViewModel(
    private val recipeId: String,
    private val recipesRepository: RecipesRepository,
    progressUseCases: ProgressUseCases,
) : MviViewModel<RecipeAction, RecipeState, RecipeEvent>(
        RecipeState(xp = progressUseCases.calculateCookXp(CookKind.Recipe, lessonXp = 0, cookedBefore = false)),
    ) {
    override fun onStateSubscription() {
        // TODO(workshop 6.4): load the recipe through recipesRepository.getRecipe while the state is still Initial, showing Loading, then Success or Error
    }

    override fun onAction(action: RecipeAction) {
        // TODO(workshop 6.4): Retry loads the recipe again, Start cooking sends StartCooking only once the recipe has loaded, and an ingredient tap ticks or unticks it
    }
}
