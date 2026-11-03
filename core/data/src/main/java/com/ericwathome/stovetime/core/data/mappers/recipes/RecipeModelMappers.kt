package com.ericwathome.stovetime.core.data.mappers.recipes

import com.ericwathome.stovetime.core.data.model.recipes.CatalogueMeal
import com.ericwathome.stovetime.core.data.model.recipes.Ingredient
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.IngredientCache
import com.ericwathome.stovetime.datasources.local.models.RecipeCache
import com.ericwathome.stovetime.datasources.network.dtos.MealDto
import com.ericwathome.stovetime.datasources.network.dtos.ingredients

internal fun MealDto.toCatalogueMealCache(
    category: String,
    syncedAt: Long,
): CatalogueMealCache =
    CatalogueMealCache(
        id = idMeal,
        name = strMeal.trim(),
        photo = strMealThumb?.ifBlank { null },
        category = category,
        syncedAt = syncedAt,
    )

internal fun CatalogueMealCache.toData(): CatalogueMeal = CatalogueMeal(id = id, name = name, photo = photo, category = category)

internal fun MealDto.toRecipeCache(fetchedAt: Long): RecipeCache =
    RecipeCache(
        id = idMeal,
        name = strMeal.trim(),
        category = strCategory?.trim()?.ifBlank { null },
        area = strArea?.trim()?.ifBlank { null },
        photo = strMealThumb?.ifBlank { null },
        instructions = strInstructions?.trim().orEmpty(),
        ingredients = ingredients().map { IngredientCache(name = it.name, measure = it.measure) },
        fetchedAt = fetchedAt,
    )

internal fun RecipeCache.toData(): Recipe =
    Recipe(
        id = id,
        name = name,
        category = category,
        area = area,
        photo = photo,
        instructions = instructions,
        ingredients = ingredients.map { Ingredient(name = it.name, measure = it.measure) },
    )
