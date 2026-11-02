package com.ericwathome.stovetime.core.data.mappers.progress

import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.progress.NewCook
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.datasources.local.models.CookCache

internal fun CookCache.toData(): Cook =
    Cook(
        id = id,
        dishId = dishId,
        kind = CookKind.entries.firstOrNull { it.name == kind } ?: CookKind.Lesson,
        name = name,
        photo = photo,
        startedAt = startedAt,
        finishedAt = finishedAt,
        xp = xp,
        rating = rating.toRating(),
        weeklyGoal = weeklyGoal,
    )

internal fun NewCook.toCache(): CookCache =
    CookCache(
        id = 0,
        dishId = dishId,
        kind = kind.name,
        name = name,
        photo = photo,
        startedAt = startedAt,
        finishedAt = finishedAt,
        xp = xp,
        rating = rating?.name,
        weeklyGoal = weeklyGoal,
    )

internal fun String?.toRating(): Rating? = Rating.entries.firstOrNull { it.name == this }
