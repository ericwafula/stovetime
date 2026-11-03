package com.ericwathome.stovetime.datasources.local.daos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ericwathome.stovetime.datasources.local.database.StovetimeDatabase
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealEntity
import com.ericwathome.stovetime.datasources.local.models.CategoryEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogueDaoTest {
    private lateinit var database: StovetimeDatabase
    private lateinit var sut: CatalogueDao

    @Before
    fun setup() {
        database =
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StovetimeDatabase::class.java).build()
        sut = database.catalogueDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun meal(
        id: String,
        name: String,
        syncedAt: Long = 1_000,
    ) = CatalogueMealEntity(id = id, name = name, photo = null, category = "Beef", syncedAt = syncedAt)

    @Test
    fun replacing_the_catalogue_upserts_and_deletes_in_one_go() =
        runTest {
            // given
            sut.replaceCatalogue(listOf(CategoryEntity("Beef", 0, 1_000)), listOf(meal("1", "Pie"), meal("2", "Stew")), emptyList())

            // when
            sut.replaceCatalogue(listOf(CategoryEntity("Beef", 0, 2_000)), listOf(meal("1", "Beef pie", syncedAt = 2_000)), listOf("2"))

            // then
            assertThat(sut.getMeals().map { it.id to it.name }).containsExactly("1" to "Beef pie")
            assertThat(sut.observeCategories().first().map { it.syncedAt }).containsExactly(2_000L)
        }

    @Test
    fun meals_are_newest_sync_first() =
        runTest {
            // when
            sut.upsertMeals(listOf(meal("1", "Pie", syncedAt = 1_000), meal("2", "Stew", syncedAt = 2_000)))

            // then
            assertThat(sut.observeMeals().first().map { it.id }).containsExactly("2", "1").inOrder()
        }
}
