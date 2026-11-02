package com.ericwathome.stovetime.core.data.repo.progress

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.progress.NewCook
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.datasources.local.helpers.LocalError
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.CookCache
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CookRepositoriesTest {
    @MockK
    private lateinit var cookLocalDatasource: CookLocalDatasource

    private val newCook =
        NewCook(
            dishId = "jammy-eggs-on-toast",
            kind = CookKind.Lesson,
            name = "Jammy eggs on toast",
            photo = "lesson_jammy_eggs_on_toast",
            startedAt = 1_000,
            finishedAt = 841_000,
            xp = 30,
        )

    private val cache =
        CookCache(
            id = 7,
            dishId = "jammy-eggs-on-toast",
            kind = "Lesson",
            name = "Jammy eggs on toast",
            photo = "lesson_jammy_eggs_on_toast",
            startedAt = 1_000,
            finishedAt = 841_000,
            xp = 30,
            rating = "PrettyGood",
        )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun saving_a_cook_returns_its_new_id() =
        runTest {
            // given
            coEvery { cookLocalDatasource.saveCook(any()) } returns LocalResult.Success(7L)

            // when
            val result = SaveCookRepositoryImpl(cookLocalDatasource)(newCook)

            // then
            assertThat(result).isEqualTo(DataResult.Success(7L))
            coVerify { cookLocalDatasource.saveCook(match { it.kind == "Lesson" && it.xp == 30 && it.rating == null }) }
        }

    @Test
    fun a_full_phone_becomes_a_storage_full_error() =
        runTest {
            // given
            coEvery { cookLocalDatasource.saveCook(any()) } returns LocalResult.Error(LocalError.StorageFull)

            // when
            val result = SaveCookRepositoryImpl(cookLocalDatasource)(newCook)

            // then
            assertThat(result).isEqualTo(DataResult.Error(DataError.StorageFull))
        }

    @Test
    fun saved_cooks_read_back_as_models() =
        runTest {
            // given
            every { cookLocalDatasource.observeCooks() } returns flowOf(LocalResult.Success(listOf(cache)))

            ObserveCooksRepositoryImpl(cookLocalDatasource)().test {
                // then
                assertThat(awaitItem()).isEqualTo(
                    DataResult.Success(
                        listOf(
                            Cook(
                                id = 7,
                                dishId = "jammy-eggs-on-toast",
                                kind = CookKind.Lesson,
                                name = "Jammy eggs on toast",
                                photo = "lesson_jammy_eggs_on_toast",
                                startedAt = 1_000,
                                finishedAt = 841_000,
                                xp = 30,
                                rating = Rating.PrettyGood,
                            ),
                        ),
                    ),
                )
                awaitComplete()
            }
        }

    @Test
    fun rating_saves_the_rating_name() =
        runTest {
            // given
            coEvery { cookLocalDatasource.rateCook(any(), any()) } returns LocalResult.Success(true)

            // when
            RateCookRepositoryImpl(cookLocalDatasource)(7, Rating.NailedIt)

            // then
            coVerify { cookLocalDatasource.rateCook(7, "NailedIt") }
        }
}
