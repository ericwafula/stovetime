package com.ericwathome.stovetime.datasources.local.helpers

import android.content.Context
import kotlinx.serialization.json.Json

internal val COURSE_ASSETS =
    listOf(
        "courses/home_basics.json",
        "courses/rice_and_grains.json",
        "courses/one_pan_dinners.json",
        "courses/kenyan_favourites.json",
    )

internal val contentJson = Json { ignoreUnknownKeys = true }

fun interface BundledContentReader {
    fun read(fileName: String): String
}

internal class AssetContentReader(
    private val context: Context,
) : BundledContentReader {
    override fun read(fileName: String): String = context.assets.open(fileName).bufferedReader().use { it.readText() }
}
