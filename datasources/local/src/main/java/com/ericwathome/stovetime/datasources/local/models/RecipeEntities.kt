package com.ericwathome.stovetime.datasources.local.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
internal data class CategoryEntity(
    @PrimaryKey
    val name: String,
    val position: Int,
    val syncedAt: Long,
)

@Entity(indices = [Index("category")])
internal data class CatalogueMealEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val photo: String?,
    val category: String,
    val syncedAt: Long,
)

@Entity
internal data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val photo: String?,
    val instructions: String,
    val ingredients: List<IngredientEntity>,
    val fetchedAt: Long,
)

@Serializable
internal data class IngredientEntity(
    val name: String,
    val measure: String,
)

data class CategoryCache(
    val name: String,
    val position: Int,
    val syncedAt: Long,
)

data class CatalogueMealCache(
    val id: String,
    val name: String,
    val photo: String?,
    val category: String,
    val syncedAt: Long,
)

data class RecipeCache(
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val photo: String?,
    val instructions: String,
    val ingredients: List<IngredientCache>,
    val fetchedAt: Long,
)

data class IngredientCache(
    val name: String,
    val measure: String,
)
