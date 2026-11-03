package com.ericwathome.stovetime.datasources.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ericwathome.stovetime.datasources.local.daos.CatalogueDao
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.daos.RecipeDao
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealEntity
import com.ericwathome.stovetime.datasources.local.models.CategoryEntity
import com.ericwathome.stovetime.datasources.local.models.CookEntity
import com.ericwathome.stovetime.datasources.local.models.RecipeEntity

@Database(
    entities = [CookEntity::class, CategoryEntity::class, CatalogueMealEntity::class, RecipeEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
@TypeConverters(StovetimeConverters::class)
internal abstract class StovetimeDatabase : RoomDatabase() {
    abstract val cookDao: CookDao
    abstract val catalogueDao: CatalogueDao
    abstract val recipeDao: RecipeDao

    companion object {
        const val NAME = "stovetime.db"
    }
}
