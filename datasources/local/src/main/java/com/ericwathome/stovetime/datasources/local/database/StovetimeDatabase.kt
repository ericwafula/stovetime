package com.ericwathome.stovetime.datasources.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.models.CookEntity

@Database(
    entities = [CookEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class StovetimeDatabase : RoomDatabase() {
    abstract val cookDao: CookDao

    companion object {
        const val NAME = "stovetime.db"
    }
}
