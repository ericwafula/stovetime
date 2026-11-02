package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.recipes.Ingredient
import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.IngredientCache
import com.ericwathome.stovetime.datasources.local.models.RecipeCache
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.RecipeLocalDatasource
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
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetRecipeRepositoryImplTest {
    @MockK
    private lateinit var recipesRemoteDatasource: RecipesRemoteDatasource

    @MockK
    private lateinit var recipeLocalDatasource: RecipeLocalDatasource

    private lateinit var sut: GetRecipeRepositoryImpl

    private val dto =
        MealDto(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken Casserole",
            strCategory = "Chicken",
            strArea = "Japanese",
            strInstructions = "Preheat oven to 350.\r\nCombine soy sauce.",
            strMealThumb = "https://www.themealdb.com/images/teriyaki.jpg",
            strIngredient1 = "soy sauce",
            strMeasure1 = "3/4 cup",
            strIngredient2 = "",
            strMeasure2 = "",
        )

    private val recipe =
        Recipe(
            id = "52772",
            name = "Teriyaki Chicken Casserole",
            category = "Chicken",
            area = "Japanese",
            photo = "https://www.themealdb.com/images/teriyaki.jpg",
            instructions = "Preheat oven to 350.\r\nCombine soy sauce.",
            ingredients = listOf(Ingredient(name = "soy sauce", measure = "3/4 cup")),
        )

    private val cache =
        RecipeCache(
            id = "52772",
            name = "Teriyaki Chicken Casserole",
            category = "Chicken",
            area = "Japanese",
            photo = "https://www.themealdb.com/images/teriyaki.jpg",
            instructions = "Preheat oven to 350.\r\nCombine soy sauce.",
            ingredients = listOf(IngredientCache(name = "soy sauce", measure = "3/4 cup")),
            fetchedAt = 1_000,
        )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { recipeLocalDatasource.getRecipe(any()) } returns LocalResult.Success(null)
        coEvery { recipeLocalDatasource.saveRecipe(any()) } returns LocalResult.Success(true)
        sut = GetRecipeRepositoryImpl(recipesRemoteDatasource, recipeLocalDatasource)
    }

    @Test
    fun a_recipe_opened_before_comes_from_the_phone_even_offline() =
        runTest {
            // given
            coEvery { recipeLocalDatasource.getRecipe("52772") } returns LocalResult.Success(cache)
            coEvery { recipesRemoteDatasource.lookupMeal(any()) } returns NetworkResult.Error(NetworkError.NoConnection)

            // when
            val result = sut("52772")

            // then
            assertThat(result).isEqualTo(DataResult.Success(recipe))
            coVerify(exactly = 0) { recipesRemoteDatasource.lookupMeal(any()) }
        }

    @Test
    fun a_recipe_opened_for_the_first_time_is_fetched_and_kept() =
        runTest {
            // given
            coEvery { recipesRemoteDatasource.lookupMeal("52772") } returns NetworkResult.Success(MealsResponseDto(listOf(dto)))

            // when
            val result = sut("52772")

            // then
            assertThat(result).isEqualTo(DataResult.Success(recipe))
            coVerify { recipeLocalDatasource.saveRecipe(match { it.id == "52772" && it.ingredients.size == 1 }) }
        }

    @Test
    fun a_recipe_opened_for_the_first_time_offline_is_a_no_connection_error() =
        runTest {
            // given
            coEvery { recipesRemoteDatasource.lookupMeal("52772") } returns NetworkResult.Error(NetworkError.NoConnection)

            // when
            val result = sut("52772")

            // then
            assertThat(result).isEqualTo(DataResult.Error(DataError.NoConnection))
            coVerify(exactly = 0) { recipeLocalDatasource.saveRecipe(any()) }
        }

    @Test
    fun an_unknown_recipe_is_an_error() =
        runTest {
            // given
            coEvery { recipesRemoteDatasource.lookupMeal("1") } returns NetworkResult.Success(MealsResponseDto(null))

            // when
            val result = sut("1")

            // then
            assertThat(result).isInstanceOf(DataResult.Error::class.java)
        }
}
