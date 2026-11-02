package com.ericwathome.stovetime.core.data.repo.recipes

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.recipes.Catalogue
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CategoryCache
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.CatalogueLocalDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObserveCatalogueRepositoryImplTest {
    @MockK
    private lateinit var catalogueLocalDatasource: CatalogueLocalDatasource

    private lateinit var sut: ObserveCatalogueRepositoryImpl

    private fun meal(
        id: String,
        name: String,
        category: String,
    ) = CatalogueMealCache(id = id, name = name, photo = null, category = category, syncedAt = 1_000)

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { catalogueLocalDatasource.observeCategories() } returns
            flowOf(LocalResult.Success(listOf(CategoryCache("Dessert", 0, 1_000), CategoryCache("Beef", 1, 1_000))))
        every { catalogueLocalDatasource.observeMeals() } returns
            flowOf(
                LocalResult.Success(
                    listOf(meal("1", "Crème Brûlée", "Dessert"), meal("2", "Beef Wellington", "Beef"), meal("3", "Eton Mess", "Dessert")),
                ),
            )
        sut = ObserveCatalogueRepositoryImpl(catalogueLocalDatasource)
    }

    private fun DataResult<Catalogue>.mealIds() = (this as DataResult.Success).data.meals.map { it.id }

    @Test
    fun all_shows_every_stored_meal_and_the_categories_in_order() =
        runTest {
            sut(category = null, query = "").test {
                // when
                val result = awaitItem() as DataResult.Success

                // then
                assertThat(result.data.categories).containsExactly("Dessert", "Beef").inOrder()
                assertThat(result.data.meals.map { it.id }).containsExactly("1", "2", "3").inOrder()
                assertThat(result.data.storedCount).isEqualTo(3)
                awaitComplete()
            }
        }

    @Test
    fun a_category_shows_only_its_meals() =
        runTest {
            sut(category = "Dessert", query = "").test {
                // then
                assertThat(awaitItem().mealIds()).containsExactly("1", "3").inOrder()
                awaitComplete()
            }
        }

    @Test
    fun search_ignores_case_and_accents() =
        runTest {
            sut(category = null, query = "CREME brulee").test {
                // then
                assertThat(awaitItem().mealIds()).containsExactly("1")
                awaitComplete()
            }
        }

    @Test
    fun a_search_with_no_matches_is_empty_but_keeps_the_stored_count() =
        runTest {
            sut(category = null, query = "xyz").test {
                // when
                val result = awaitItem() as DataResult.Success

                // then
                assertThat(result.data.meals).isEmpty()
                assertThat(result.data.storedCount).isEqualTo(3)
                awaitComplete()
            }
        }
}
