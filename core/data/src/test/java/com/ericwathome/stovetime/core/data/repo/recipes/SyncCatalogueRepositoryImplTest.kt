package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CategoryCache
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.CatalogueLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.settings.RecipeSyncLocalDatasource
import com.ericwathome.stovetime.datasources.network.dtos.CategoriesResponseDto
import com.ericwathome.stovetime.datasources.network.dtos.CategoryDto
import com.ericwathome.stovetime.datasources.network.dtos.MealDto
import com.ericwathome.stovetime.datasources.network.dtos.MealsResponseDto
import com.ericwathome.stovetime.datasources.network.helpers.NetworkError
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.sources.recipes.RecipesRemoteDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncCatalogueRepositoryImplTest {
    @MockK
    private lateinit var recipesRemoteDatasource: RecipesRemoteDatasource

    @MockK
    private lateinit var catalogueLocalDatasource: CatalogueLocalDatasource

    @MockK
    private lateinit var cookLocalDatasource: CookLocalDatasource

    @MockK
    private lateinit var recipeSyncLocalDatasource: RecipeSyncLocalDatasource

    private lateinit var sut: SyncCatalogueRepositoryImpl

    private val upserts = slot<List<CatalogueMealCache>>()
    private val deleteIds = slot<List<String>>()
    private val categories = slot<List<CategoryCache>>()

    private fun meal(
        id: String,
        name: String,
    ) = MealDto(idMeal = id, strMeal = name, strMealThumb = "https://www.themealdb.com/images/$id.jpg")

    private fun stored(
        id: String,
        name: String,
        category: String,
    ) = CatalogueMealCache(id = id, name = name, photo = "https://www.themealdb.com/images/$id.jpg", category = category, syncedAt = 1_000)

    private fun givenRemote(vararg mealsByCategory: Pair<String, List<MealDto>?>) {
        coEvery { recipesRemoteDatasource.getCategories() } returns
            NetworkResult.Success(
                CategoriesResponseDto(mealsByCategory.mapIndexed { index, (name, _) -> CategoryDto(idCategory = "$index", strCategory = name) }),
            )
        mealsByCategory.forEach { (category, meals) ->
            coEvery { recipesRemoteDatasource.getMealsByCategory(category) } returns NetworkResult.Success(MealsResponseDto(meals))
        }
    }

    private fun givenStored(vararg meals: CatalogueMealCache) {
        coEvery { catalogueLocalDatasource.getMeals() } returns LocalResult.Success(meals.toList())
    }

    private fun givenCooked(vararg ids: String) {
        coEvery { cookLocalDatasource.getCookedRecipeIds() } returns LocalResult.Success(ids.toSet())
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        givenStored()
        givenCooked()
        coEvery {
            catalogueLocalDatasource.replaceCatalogue(capture(categories), capture(upserts), capture(deleteIds))
        } returns LocalResult.Success(true)
        coEvery { recipeSyncLocalDatasource.lastSyncedAt.save(any()) } returns LocalResult.Success(true)
        coEvery { recipeSyncLocalDatasource.lastFailedAt.save(any()) } returns LocalResult.Success(true)
        sut = SyncCatalogueRepositoryImpl(recipesRemoteDatasource, catalogueLocalDatasource, cookLocalDatasource, recipeSyncLocalDatasource)
    }

    @Test
    fun the_first_sync_fills_the_catalogue() =
        runTest {
            // given
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")), "Pasta" to listOf(meal("2", "Lasagne")))

            // when
            val result = sut()

            // then
            assertThat(result).isEqualTo(DataResult.Success(Unit))
            assertThat(categories.captured.map { it.name }).containsExactly("Beef", "Pasta").inOrder()
            assertThat(upserts.captured.map { it.id to it.category }).containsExactly("1" to "Beef", "2" to "Pasta")
            assertThat(deleteIds.captured).isEmpty()
        }

    @Test
    fun a_second_sync_updates_changed_meals_without_duplicating() =
        runTest {
            // given
            givenStored(stored("1", "Beef Wellington", "Beef"), stored("2", "Lasagne", "Pasta"))
            givenRemote(
                "Beef" to listOf(meal("1", "Beef Wellington")),
                "Pasta" to listOf(meal("2", "Lasagne al forno"), meal("3", "Carbonara")),
            )

            // when
            sut()

            // then
            assertThat(upserts.captured.map { it.id to it.name }).containsExactly("2" to "Lasagne al forno", "3" to "Carbonara")
        }

    @Test
    fun a_meal_listed_in_two_categories_is_stored_once() =
        runTest {
            // given
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")), "Miscellaneous" to listOf(meal("1", "Beef Wellington")))

            // when
            sut()

            // then
            assertThat(upserts.captured.map { it.id }).containsExactly("1")
        }

    @Test
    fun a_meal_removed_from_themealdb_is_deleted() =
        runTest {
            // given
            givenStored(stored("1", "Beef Wellington", "Beef"), stored("9", "Old pie", "Beef"))
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")))

            // when
            sut()

            // then
            assertThat(deleteIds.captured).containsExactly("9")
        }

    @Test
    fun a_removed_meal_the_user_has_cooked_is_kept() =
        runTest {
            // given
            givenStored(stored("1", "Beef Wellington", "Beef"), stored("9", "Old pie", "Beef"))
            givenCooked("9")
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")))

            // when
            sut()

            // then
            assertThat(deleteIds.captured).isEmpty()
        }

    @Test
    fun a_category_with_no_meals_is_stored_as_empty() =
        runTest {
            // given
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")), "Goat" to null)

            // when
            val result = sut()

            // then
            assertThat(result).isEqualTo(DataResult.Success(Unit))
            assertThat(categories.captured.map { it.name }).containsExactly("Beef", "Goat").inOrder()
        }

    @Test
    fun a_failed_sync_keeps_the_old_data() =
        runTest {
            // given
            givenStored(stored("1", "Beef Wellington", "Beef"))
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")))
            coEvery { recipesRemoteDatasource.getMealsByCategory("Beef") } returns NetworkResult.Error(NetworkError.NoConnection)

            // when
            val result = sut()

            // then
            assertThat(result).isEqualTo(DataResult.Error(DataError.NoConnection))
            coVerify(exactly = 0) { catalogueLocalDatasource.replaceCatalogue(any(), any(), any()) }
            coVerify { recipeSyncLocalDatasource.lastFailedAt.save(any()) }
            coVerify(exactly = 0) { recipeSyncLocalDatasource.lastSyncedAt.save(any()) }
        }

    @Test
    fun a_successful_sync_stores_its_time() =
        runTest {
            // given
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")))

            // when
            sut()

            // then
            coVerify { recipeSyncLocalDatasource.lastSyncedAt.save(any()) }
        }

    @Test
    fun two_syncs_asked_for_at_once_run_only_once() =
        runTest {
            // given
            givenRemote("Beef" to listOf(meal("1", "Beef Wellington")))
            coEvery { recipesRemoteDatasource.getCategories() } coAnswers {
                delay(1_000)
                NetworkResult.Success(CategoriesResponseDto(listOf(CategoryDto(idCategory = "1", strCategory = "Beef"))))
            }

            // when
            val first = async { sut() }
            val second = async { sut() }
            first.await()
            second.await()

            // then
            coVerify(exactly = 1) { recipesRemoteDatasource.getCategories() }
        }

    @Test
    fun a_second_caller_gets_the_failure_of_the_sync_it_waited_for() =
        runTest {
            // given
            coEvery { recipesRemoteDatasource.getCategories() } coAnswers {
                delay(1_000)
                NetworkResult.Error(NetworkError.Timeout)
            }

            // when
            val first = async { sut() }
            val second = async { sut() }

            // then
            assertThat(first.await()).isEqualTo(DataResult.Error(DataError.Timeout))
            assertThat(second.await()).isEqualTo(DataResult.Error(DataError.Timeout))
        }

    @Test
    fun an_empty_catalogue_from_themealdb_keeps_the_stored_one() =
        runTest {
            // given
            givenStored(stored("1", "Beef Wellington", "Beef"))
            coEvery { recipesRemoteDatasource.getCategories() } returns NetworkResult.Success(CategoriesResponseDto(emptyList()))

            // when
            val result = sut()

            // then
            assertThat(result).isInstanceOf(DataResult.Error::class.java)
            coVerify(exactly = 0) { catalogueLocalDatasource.replaceCatalogue(any(), any(), any()) }
        }

    @Test
    fun categories_with_no_meals_at_all_keep_the_stored_catalogue() =
        runTest {
            // given
            givenStored(stored("1", "Beef Wellington", "Beef"))
            givenRemote("Beef" to null, "Goat" to null)

            // when
            val result = sut()

            // then
            assertThat(result).isInstanceOf(DataResult.Error::class.java)
            coVerify(exactly = 0) { catalogueLocalDatasource.replaceCatalogue(any(), any(), any()) }
        }
}
