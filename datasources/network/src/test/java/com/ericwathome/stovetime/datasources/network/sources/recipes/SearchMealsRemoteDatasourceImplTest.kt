package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.datasources.network.dtos.MealsResponseDto
import com.ericwathome.stovetime.datasources.network.helpers.NetworkError
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.createHttpClient
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.ericwathome.stovetime.testing.createMockEngine
import com.ericwathome.stovetime.testing.emptyMealsResponse
import com.ericwathome.stovetime.testing.malformedMealsResponse
import com.ericwathome.stovetime.testing.successfulMealLookupResponse
import com.google.common.truth.Truth.assertThat
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class SearchMealsRemoteDatasourceImplTest {
    private fun TestScope.createSut(engine: MockEngine) =
        SearchMealsRemoteDatasourceImpl(
            dispatchers = TestStovetimeDispatchers(testScheduler),
            client = createHttpClient(engine, isDebug = false),
        )

    @Test
    fun search_meals_returns_the_matching_meals() =
        runTest {
            // given
            val sut = createSut(createMockEngine(responseBody = successfulMealLookupResponse))

            // when
            val result = sut(query = "teriyaki")

            // then
            val meals = (result as NetworkResult.Success).data.meals.orEmpty()
            assertThat(meals.map { it.idMeal }).containsExactly("52772")
        }

    @Test
    fun a_search_with_no_hits_returns_an_empty_result_not_an_error() =
        runTest {
            // given
            val sut = createSut(createMockEngine(responseBody = emptyMealsResponse))

            // when
            val result = sut(query = "nothing")

            // then
            assertThat(result).isEqualTo(NetworkResult.Success(MealsResponseDto(meals = null)))
        }

    @Test
    fun search_text_with_spaces_and_accents_is_encoded() =
        runTest {
            // given
            var requestedUrl: Url? = null
            val sut = createSut(createMockEngine(responseBody = emptyMealsResponse) { requestedUrl = it.url })

            // when
            sut(query = "crème brûlée")

            // then
            assertThat(requestedUrl.toString()).isEqualTo("https://www.themealdb.com/api/json/v1/1/search.php?s=cr%C3%A8me+br%C3%BBl%C3%A9e")
            assertThat(requestedUrl?.parameters?.get("s")).isEqualTo("crème brûlée")
        }

    @Test
    fun no_connection_returns_a_no_connection_error() =
        runTest {
            // given
            val sut = createSut(createMockEngine(throwable = UnresolvedAddressException()))

            // when
            val result = sut(query = "chicken")

            // then
            assertThat(result).isEqualTo(NetworkResult.Error(NetworkError.NoConnection))
        }

    @Test
    fun a_timeout_returns_a_timeout_error() =
        runTest {
            // given
            val sut = createSut(createMockEngine(throwable = HttpRequestTimeoutException("search.php", 15_000L)))

            // when
            val result = sut(query = "chicken")

            // then
            assertThat(result).isEqualTo(NetworkResult.Error(NetworkError.Timeout))
        }

    @Test
    fun a_server_error_returns_a_server_error_with_its_code() =
        runTest {
            // given
            val sut = createSut(createMockEngine(status = HttpStatusCode.InternalServerError))

            // when
            val result = sut(query = "chicken")

            // then
            assertThat(result).isEqualTo(NetworkResult.Error(NetworkError.Server(500)))
        }

    @Test
    fun an_unreadable_body_returns_an_unknown_error() =
        runTest {
            // given
            val sut = createSut(createMockEngine(responseBody = malformedMealsResponse))

            // when
            val result = sut(query = "chicken")

            // then
            assertThat((result as NetworkResult.Error).error).isInstanceOf(NetworkError.Unknown::class.java)
        }

    @Test(expected = CancellationException::class)
    fun a_cancelled_search_is_not_turned_into_an_error() =
        runTest {
            createSut(createMockEngine(throwable = CancellationException("left the screen")))(query = "chicken")
        }
}
