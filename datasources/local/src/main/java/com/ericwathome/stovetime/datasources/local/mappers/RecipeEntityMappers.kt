package com.ericwathome.stovetime.datasources.local.mappers

import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealEntity
import com.ericwathome.stovetime.datasources.local.models.CategoryCache
import com.ericwathome.stovetime.datasources.local.models.CategoryEntity
import com.ericwathome.stovetime.datasources.local.models.IngredientCache
import com.ericwathome.stovetime.datasources.local.models.IngredientEntity
import com.ericwathome.stovetime.datasources.local.models.RecipeCache
import com.ericwathome.stovetime.datasources.local.models.RecipeEntity

internal fun CategoryEntity.toCache(): CategoryCache = CategoryCache(name = name, position = position, syncedAt = syncedAt)

internal fun CategoryCache.toEntity(): CategoryEntity = CategoryEntity(name = name, position = position, syncedAt = syncedAt)

internal fun CatalogueMealEntity.toCache(): CatalogueMealCache =
    CatalogueMealCache(id = id, name = name, photo = photo, category = category, syncedAt = syncedAt)

internal fun CatalogueMealCache.toEntity(): CatalogueMealEntity =
    CatalogueMealEntity(id = id, name = name, photo = photo, category = category, syncedAt = syncedAt)

internal fun RecipeEntity.toCache(): RecipeCache =
    RecipeCache(
        id = id,
        name = name,
        category = category,
        area = area,
        photo = photo,
        instructions = instructions,
        ingredients = ingredients.map { IngredientCache(name = it.name, measure = it.measure) },
        fetchedAt = fetchedAt,
    )

internal fun RecipeCache.toEntity(): RecipeEntity =
    RecipeEntity(
        id = id,
        name = name,
        category = category,
        area = area,
        photo = photo,
        instructions = instructions,
        ingredients = ingredients.map { IngredientEntity(name = it.name, measure = it.measure) },
        fetchedAt = fetchedAt,
    )
