package com.ericwathome.stovetime.features.recipes.recipe

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.model.recipes.RECIPE_COOK_XP
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import kotlinx.coroutines.launch

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
    ) : RecipeEvent
}

data class RecipeState(
    val recipe: ViewState<Recipe> = ViewState.Initial,
    val checkedIngredients: Set<Int> = emptySet(),
    val xp: Int = RECIPE_COOK_XP,
) {
    val isStartEnabled: Boolean
        get() = recipe is ViewState.Success
}

class RecipeViewModel(
    private val recipeId: String,
    private val recipesRepository: RecipesRepository,
) : MviViewModel<RecipeAction, RecipeState, RecipeEvent>(RecipeState()) {
    override fun onStateSubscription() {
        if (currentState.recipe == ViewState.Initial) loadRecipe()
    }

    override fun onAction(action: RecipeAction) {
        when (action) {
            RecipeAction.OnClickRetry -> {
                loadRecipe()
            }

            RecipeAction.OnClickStartCooking -> {
                onClickStartCooking()
            }

            is RecipeAction.OnToggleIngredient -> {
                onToggleIngredient(action.index)
            }
        }
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            updateState { it.copy(recipe = ViewState.Loading) }
            recipesRepository
                .getRecipe(recipeId)
                .onSuccess { recipe -> updateState { it.copy(recipe = ViewState.Success(recipe)) } }
                .onError { error -> updateState { it.copy(recipe = ViewState.Error(error)) } }
        }
    }

    private fun onClickStartCooking() {
        if (!currentState.isStartEnabled) return
        sendEvent { RecipeEvent.StartCooking(recipeId) }
    }

    private fun onToggleIngredient(index: Int) {
        updateState {
            val checked = if (index in it.checkedIngredients) it.checkedIngredients - index else it.checkedIngredients + index
            it.copy(checkedIngredients = checked)
        }
    }
}
