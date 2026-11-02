package com.ericwathome.stovetime.datasources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ericwathome.stovetime.datasources.local.models.CookEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CookDao {
    @Insert
    suspend fun insertCook(item: CookEntity): Long

    @Query("UPDATE cookentity SET rating = :rating WHERE id = :id")
    suspend fun updateRating(
        id: Long,
        rating: String?,
    )

    @Query("SELECT * FROM cookentity ORDER BY finishedAt DESC, id DESC")
    fun observeCooks(): Flow<List<CookEntity>>

    @Query("DELETE FROM cookentity")
    suspend fun deleteAllCooks()
}
