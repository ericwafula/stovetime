package com.ericwathome.stovetime.features.recipes.browse

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.helpers.matchesSearch
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.ObserveConnectivity
import com.ericwathome.stovetime.core.data.model.recipes.Catalogue
import com.ericwathome.stovetime.core.data.model.recipes.CatalogueMeal
import com.ericwathome.stovetime.core.data.model.recipes.RecipeSyncStatus
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import com.ericwathome.stovetime.core.data.workers.recipes.StartRecipeSyncWork
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipesBrowseViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var recipesRepository: RecipesRepository

    @MockK
    private lateinit var startRecipeSyncWork: StartRecipeSyncWork

    @MockK
    private lateinit var observeConnectivity: ObserveConnectivity

    private lateinit var sut: RecipesBrowseViewModel

    private val stored = MutableStateFlow(emptyList<CatalogueMeal>())
    private val syncStatus = MutableStateFlow(RecipeSyncStatus(lastSyncedAt = 1_000))
    private val isOnline = MutableStateFlow(true)

    private val creme = CatalogueMeal(id = "1", name = "Crème Brûlée", photo = null, category = "Dessert")
    private val wellington = CatalogueMeal(id = "2", name = "Beef Wellington", photo = null, category = "Beef")

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { recipesRepository.observeCatalogue(any(), any()) } answers {
            val category = firstArg<String?>()
            val query = secondArg<String>()
            stored.map { meals ->
                DataResult.Success(
                    Catalogue(
                        categories = listOf("Dessert", "Beef"),
                        meals = meals.filter { (category == null || it.category == category) && it.name.matchesSearch(query) },
                        storedCount = meals.size,
                    ),
                )
            }
        }
        every { recipesRepository.observeSyncStatus() } returns syncStatus
        every { observeConnectivity() } returns isOnline
        sut = RecipesBrowseViewModel(recipesRepository, startRecipeSyncWork, observeConnectivity)
    }

    @Test
    fun the_tab_opens_on_all_with_every_stored_meal() =
        runTest {
            // given
            stored.value = listOf(creme, wellington)

            sut.state.test {
                // then
                val state = expectMostRecentItem()
                assertThat(state.selectedCategory).isNull()
                assertThat(state.content).isEqualTo(BrowseContent.Meals(listOf(creme, wellington)))
            }
        }

    @Test
    fun tapping_a_category_shows_only_its_meals() =
        runTest {
            // given
            stored.value = listOf(creme, wellington)

            sut.state.test {
                // when
                sut.onAction(RecipesBrowseAction.OnSelectCategory("Beef"))

                // then
                assertThat(expectMostRecentItem().content).isEqualTo(BrowseContent.Meals(listOf(wellington)))
            }
        }

    @Test
    fun search_ignores_case_and_accents() =
        runTest {
            // given
            stored.value = listOf(creme, wellington)

            sut.state.test {
                // when
                sut.onAction(RecipesBrowseAction.OnChangeQuery("CREME"))

                // then
                assertThat(expectMostRecentItem().content).isEqualTo(BrowseContent.Meals(listOf(creme)))
            }
        }

    @Test
    fun a_search_with_no_matches_says_so() =
        runTest {
            // given
            stored.value = listOf(creme, wellington)

            sut.state.test {
                // when
                sut.onAction(RecipesBrowseAction.OnChangeQuery("xyz "))

                // then
                assertThat(expectMostRecentItem().content).isEqualTo(BrowseContent.NoMatches("xyz"))
            }
        }

    @Test
    fun pull_to_refresh_starts_a_sync() =
        runTest {
            // when
            sut.onAction(RecipesBrowseAction.OnRefresh)

            // then
            verify { startRecipeSyncWork() }
        }

    @Test
    fun retry_starts_a_sync() =
        runTest {
            // when
            sut.onAction(RecipesBrowseAction.OnClickRetry)

            // then
            verify { startRecipeSyncWork() }
        }

    @Test
    fun nothing_stored_while_the_first_sync_runs_is_loading() =
        runTest {
            // given
            syncStatus.value = RecipeSyncStatus(isSyncing = true)

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().content).isEqualTo(BrowseContent.Loading)
            }
        }

    @Test
    fun nothing_stored_and_offline_explains_why() =
        runTest {
            // given
            syncStatus.value = RecipeSyncStatus()
            isOnline.value = false

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().content).isEqualTo(BrowseContent.Empty(isOffline = true))
            }
        }

    @Test
    fun nothing_stored_after_a_failed_sync_offers_a_retry() =
        runTest {
            // given
            syncStatus.value = RecipeSyncStatus(lastFailedAt = 1_000)

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().content).isEqualTo(BrowseContent.Empty(isOffline = false))
            }
        }

    @Test
    fun the_header_shows_when_it_last_synced() =
        runTest {
            sut.state.test {
                // then
                assertThat(expectMostRecentItem().lastSynced).isNotNull()
            }
        }

    @Test
    fun tapping_a_card_opens_its_recipe() =
        runTest {
            sut.event.test {
                // when
                sut.onAction(RecipesBrowseAction.OnClickRecipe("52772"))

                // then
                assertThat(awaitItem()).isEqualTo(RecipesBrowseEvent.OpenRecipe("52772"))
            }
        }
}
