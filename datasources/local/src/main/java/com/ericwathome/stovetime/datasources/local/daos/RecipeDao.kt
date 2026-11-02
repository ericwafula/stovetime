package com.ericwathome.stovetime.datasources.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ericwathome.stovetime.datasources.local.models.RecipeEntity

@Dao
internal interface RecipeDao {
    @Query("SELECT * FROM recipeentity WHERE id = :id")
    suspend fun getRecipe(id: String): RecipeEntity?

    @Upsert
    suspend fun upsertRecipe(item: RecipeEntity)
}
