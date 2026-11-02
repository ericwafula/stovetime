package com.ericwathome.stovetime.datasources.local.database

import androidx.room.TypeConverter
import com.ericwathome.stovetime.datasources.local.models.IngredientEntity
import kotlinx.serialization.json.Json

internal class StovetimeConverters {
    @TypeConverter
    fun fromIngredients(ingredients: List<IngredientEntity>): String = Json.encodeToString(ingredients)

    @TypeConverter
    fun toIngredients(json: String): List<IngredientEntity> = Json.decodeFromString(json)
}
