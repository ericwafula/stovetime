package com.ericwathome.stovetime.datasources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealEntity
import com.ericwathome.stovetime.datasources.local.models.CategoryEntity
import kotlinx.coroutines.flow.Flow

private const val DELETE_CHUNK_SIZE = 500

@Dao
internal interface CatalogueDao {
    @Query("SELECT * FROM categoryentity ORDER BY position ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM cataloguemealentity ORDER BY syncedAt DESC, name COLLATE NOCASE ASC")
    fun observeMeals(): Flow<List<CatalogueMealEntity>>

    @Query("SELECT * FROM cataloguemealentity")
    suspend fun getMeals(): List<CatalogueMealEntity>

    @Query("DELETE FROM categoryentity")
    suspend fun deleteAllCategories()

    @Insert
    suspend fun insertCategories(items: List<CategoryEntity>)

    @Upsert
    suspend fun upsertMeals(items: List<CatalogueMealEntity>)

    @Query("DELETE FROM cataloguemealentity WHERE id IN (:ids)")
    suspend fun deleteMeals(ids: List<String>)

    @Transaction
    suspend fun replaceCatalogue(
        categories: List<CategoryEntity>,
        upserts: List<CatalogueMealEntity>,
        deleteIds: List<String>,
    ) {
        deleteAllCategories()
        insertCategories(categories)
        upsertMeals(upserts)
        deleteIds.chunked(DELETE_CHUNK_SIZE).forEach { deleteMeals(it) }
    }
}
