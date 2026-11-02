package com.ericwathome.stovetime.datasources.network.helpers

import com.ericwathome.stovetime.datasources.network.BuildConfig

internal sealed class Route(
    path: String,
) {
    val url: String = BuildConfig.MEALDB_BASE_URL + path

    data object Categories : Route("categories.php")

    data object FilterMeals : Route("filter.php")

    data object SearchMeals : Route("search.php")

    data object LookupMeal : Route("lookup.php")
}
