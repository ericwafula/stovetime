package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.datasources.network.dtos.IngredientDto
import com.ericwathome.stovetime.datasources.network.dtos.ingredients
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.createHttpClient
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.ericwathome.stovetime.testing.createMockEngine
import com.ericwathome.stovetime.testing.emptyMealsResponse
import com.ericwathome.stovetime.testing.successfulMealLookupResponse
import com.google.common.truth.Truth.assertThat
import io.ktor.http.Url
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LookupMealRemoteDatasourceImplTest {
    @Test
    fun lookup_meal_returns_the_full_meal() =
        runTest {
            // given
            val engine = createMockEngine(responseBody = successfulMealLookupResponse)
            val sut = LookupMealRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            val result = sut(id = "52772")

            // then
            val meal = (result as NetworkResult.Success).data.meals!!.single()
            assertThat(meal.strMeal).isEqualTo("Teriyaki Chicken Casserole")
            assertThat(meal.strArea).isEqualTo("Japanese")
            assertThat(meal.strYoutube).isEqualTo("https://www.youtube.com/watch?v=4aZr5hZXP_s")
        }

    @Test
    fun a_looked_up_meal_pairs_its_ingredients_and_drops_empty_slots() =
        runTest {
            // given
            val engine = createMockEngine(responseBody = successfulMealLookupResponse)
            val sut = LookupMealRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            val meal = (sut(id = "52772") as NetworkResult.Success).data.meals!!.single()

            // then
            assertThat(meal.ingredients())
                .containsExactly(
                    IngredientDto(name = "soy sauce", measure = "3/4 cup"),
                    IngredientDto(name = "water", measure = "1/2 cup"),
                    IngredientDto(name = "brown sugar", measure = "1/4 cup"),
                    IngredientDto(name = "salt", measure = ""),
                ).inOrder()
        }

    @Test
    fun lookup_meal_asks_for_the_id() =
        runTest {
            // given
            var requestedUrl: Url? = null
            val engine = createMockEngine(responseBody = successfulMealLookupResponse) { requestedUrl = it.url }
            val sut = LookupMealRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            sut(id = "52772")

            // then
            assertThat(requestedUrl.toString()).isEqualTo("https://www.themealdb.com/api/json/v1/1/lookup.php?i=52772")
        }

    @Test
    fun an_unknown_id_returns_an_empty_result_not_an_error() =
        runTest {
            // given
            val engine = createMockEngine(responseBody = emptyMealsResponse)
            val sut = LookupMealRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            val result = sut(id = "0")

            // then
            assertThat((result as NetworkResult.Success).data.meals).isNull()
        }
}
