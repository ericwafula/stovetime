package com.ericwathome.stovetime.datasources.local.helpers

import android.content.Context
import kotlinx.serialization.json.Json

internal const val HOME_BASICS_COURSE_ASSET = "courses/home_basics.json"

internal val contentJson = Json { ignoreUnknownKeys = true }

fun interface BundledContentReader {
    fun read(fileName: String): String
}

internal class AssetContentReader(
    private val context: Context,
) : BundledContentReader {
    override fun read(fileName: String): String = context.assets.open(fileName).bufferedReader().use { it.readText() }
}
