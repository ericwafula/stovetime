package com.ericwathome.stovetime.datasources.local.daos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ericwathome.stovetime.datasources.local.database.StovetimeDatabase
import com.ericwathome.stovetime.datasources.local.models.CookEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CookDaoTest {
    private lateinit var database: StovetimeDatabase
    private lateinit var sut: CookDao

    @Before
    fun setup() {
        database =
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StovetimeDatabase::class.java).build()
        sut = database.cookDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun cook(finishedAt: Long) =
        CookEntity(
            dishId = "jammy-eggs-on-toast",
            kind = "Lesson",
            name = "Jammy eggs on toast",
            photo = "lesson_jammy_eggs_on_toast",
            startedAt = finishedAt - 840_000,
            finishedAt = finishedAt,
            xp = 30,
            rating = null,
        )

    @Test
    fun a_saved_cook_reads_back() =
        runTest {
            // when
            val id = sut.insertCook(cook(finishedAt = 1_000_000))

            // then
            assertThat(sut.observeCooks().first()).containsExactly(cook(finishedAt = 1_000_000).copy(id = id))
        }

    @Test
    fun two_cooks_of_the_same_lesson_are_two_records_newest_first() =
        runTest {
            // when
            sut.insertCook(cook(finishedAt = 1_000_000))
            sut.insertCook(cook(finishedAt = 2_000_000))

            // then
            assertThat(sut.observeCooks().first().map { it.finishedAt }).containsExactly(2_000_000L, 1_000_000L).inOrder()
        }

    @Test
    fun rating_a_cook_updates_only_that_cook() =
        runTest {
            // given
            val first = sut.insertCook(cook(finishedAt = 1_000_000))
            sut.insertCook(cook(finishedAt = 2_000_000))

            // when
            sut.updateRating(first, "NailedIt")

            // then
            assertThat(sut.observeCooks().first().map { it.rating }).containsExactly(null, "NailedIt").inOrder()
        }

    @Test
    fun deleting_all_cooks_empties_the_table() =
        runTest {
            // given
            sut.insertCook(cook(finishedAt = 1_000_000))

            // when
            sut.deleteAllCooks()

            // then
            assertThat(sut.observeCooks().first()).isEmpty()
        }
}
