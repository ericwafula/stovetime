package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.createHttpClient
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.ericwathome.stovetime.testing.createMockEngine
import com.ericwathome.stovetime.testing.emptyMealsResponse
import com.ericwathome.stovetime.testing.successfulMealsByCategoryResponse
import com.google.common.truth.Truth.assertThat
import io.ktor.http.Url
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetMealsByCategoryRemoteDatasourceImplTest {
    @Test
    fun get_meals_by_category_returns_the_short_meals() =
        runTest {
            // given
            val engine = createMockEngine(responseBody = successfulMealsByCategoryResponse)
            val sut = GetMealsByCategoryRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            val result = sut(category = "Dessert")

            // then
            val meals = (result as NetworkResult.Success).data.meals.orEmpty()
            assertThat(meals.map { it.idMeal }).containsExactly("52768", "52767").inOrder()
            assertThat(meals.first().strMeal).isEqualTo("Apple Frangipan Tart")
            assertThat(meals.first().strInstructions).isNull()
        }

    @Test
    fun get_meals_by_category_filters_by_the_category() =
        runTest {
            // given
            var requestedUrl: Url? = null
            val engine = createMockEngine(responseBody = successfulMealsByCategoryResponse) { requestedUrl = it.url }
            val sut = GetMealsByCategoryRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            sut(category = "Dessert")

            // then
            assertThat(requestedUrl.toString()).isEqualTo("https://www.themealdb.com/api/json/v1/1/filter.php?c=Dessert")
        }

    @Test
    fun an_unknown_category_returns_an_empty_result_not_an_error() =
        runTest {
            // given
            val engine = createMockEngine(responseBody = emptyMealsResponse)
            val sut = GetMealsByCategoryRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            val result = sut(category = "Nothing")

            // then
            assertThat((result as NetworkResult.Success).data.meals).isNull()
        }
}
