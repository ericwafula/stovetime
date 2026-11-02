package com.ericwathome.stovetime.datasources.local.mappers

import com.ericwathome.stovetime.datasources.local.models.CookCache
import com.ericwathome.stovetime.datasources.local.models.CookEntity

internal fun CookEntity.toCache(): CookCache =
    CookCache(
        id = id,
        dishId = dishId,
        kind = kind,
        name = name,
        photo = photo,
        startedAt = startedAt,
        finishedAt = finishedAt,
        xp = xp,
        rating = rating,
    )

internal fun CookCache.toEntity(): CookEntity =
    CookEntity(
        id = id,
        dishId = dishId,
        kind = kind,
        name = name,
        photo = photo,
        startedAt = startedAt,
        finishedAt = finishedAt,
        xp = xp,
        rating = rating,
    )
