package com.ericwathome.stovetime.navigation.entry

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.features.recipes.browse.RecipesBrowseRoute
import com.ericwathome.stovetime.navigation.Destination

fun navigationRecipesBrowse(
    key: Destination.Recipes,
    onOpenRecipe: (recipeId: String) -> Unit,
): NavEntry<NavKey> = NavEntry(key) { RecipesBrowseRoute(onOpenRecipe = onOpenRecipe) }
