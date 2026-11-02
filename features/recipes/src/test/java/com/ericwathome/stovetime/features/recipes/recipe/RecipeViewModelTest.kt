package com.ericwathome.stovetime.features.recipes.recipe

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.recipes.Ingredient
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.google.common.truth.Truth.assertThat
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var recipesRepository: RecipesRepository

    @MockK
    private lateinit var progressUseCases: ProgressUseCases

    private lateinit var sut: RecipeViewModel

    private val recipe =
        Recipe(
            id = "52772",
            name = "Teriyaki Chicken Casserole",
            category = "Chicken",
            area = "Japanese",
            photo = null,
            instructions = "Preheat oven to 350° F.",
            ingredients = listOf(Ingredient(name = "soy sauce", measure = "3/4 cup"), Ingredient(name = "water", measure = "1/2 cup")),
        )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { progressUseCases.calculateCookXp(CookKind.Recipe, any(), any()) } returns 10
        coEvery { recipesRepository.getRecipe("52772") } returns DataResult.Success(recipe)
        sut = RecipeViewModel(recipeId = "52772", recipesRepository = recipesRepository, progressUseCases = progressUseCases)
    }

    @Test
    fun a_recipe_opened_before_shows_straight_away_offline() =
        runTest {
            sut.state.test {
                // then
                assertThat(expectMostRecentItem().recipe).isEqualTo(ViewState.Success(recipe))
            }
        }

    @Test
    fun a_recipe_opened_for_the_first_time_loads_then_shows() =
        runTest {
            // given
            val response = CompletableDeferred<DataResult<Recipe>>()
            coEvery { recipesRepository.getRecipe("52772") } coAnswers { response.await() }

            sut.state.test {
                assertThat(expectMostRecentItem().recipe).isEqualTo(ViewState.Loading)

                // when
                response.complete(DataResult.Success(recipe))

                // then
                assertThat(awaitItem().recipe).isEqualTo(ViewState.Success(recipe))
            }
        }

    @Test
    fun a_recipe_opened_for_the_first_time_offline_shows_the_connection_error() =
        runTest {
            // given
            coEvery { recipesRepository.getRecipe("52772") } returns DataResult.Error(DataError.NoConnection)

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().recipe).isEqualTo(ViewState.Error(DataError.NoConnection))
            }
        }

    @Test
    fun retry_loads_the_recipe_again() =
        runTest {
            // given
            coEvery { recipesRepository.getRecipe("52772") } returns DataResult.Error(DataError.NoConnection)

            sut.state.test {
                expectMostRecentItem()
                coEvery { recipesRepository.getRecipe("52772") } returns DataResult.Success(recipe)

                // when
                sut.onAction(RecipeAction.OnClickRetry)

                // then
                assertThat(expectMostRecentItem().recipe).isEqualTo(ViewState.Success(recipe))
            }
        }

    @Test
    fun ticking_an_ingredient_marks_it_and_ticking_again_clears_it() =
        runTest {
            sut.state.test {
                // when
                sut.onAction(RecipeAction.OnToggleIngredient(1))

                // then
                assertThat(expectMostRecentItem().checkedIngredients).containsExactly(1)
                sut.onAction(RecipeAction.OnToggleIngredient(1))
                assertThat(expectMostRecentItem().checkedIngredients).isEmpty()
            }
        }

    @Test
    fun start_cooking_opens_cook_mode_for_this_recipe() =
        runTest {
            sut.state.test {
                expectMostRecentItem()
                sut.event.test {
                    // when
                    sut.onAction(RecipeAction.OnClickStartCooking)

                    // then
                    val event = awaitItem() as RecipeEvent.StartCooking
                    assertThat(event.recipeId).isEqualTo("52772")
                    assertThat(event.startedAt).isGreaterThan(0L)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun the_recipe_page_shows_ten_xp() =
        runTest {
            // then
            assertThat(sut.state.value.xp).isEqualTo(10)
        }
}
