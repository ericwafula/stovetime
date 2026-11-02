package com.ericwathome.stovetime.datasources.network.sources.recipes

import com.ericwathome.stovetime.datasources.network.helpers.NetworkError
import com.ericwathome.stovetime.datasources.network.helpers.NetworkResult
import com.ericwathome.stovetime.datasources.network.helpers.createHttpClient
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.ericwathome.stovetime.testing.createMockEngine
import com.ericwathome.stovetime.testing.successfulCategoriesResponse
import com.google.common.truth.Truth.assertThat
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetCategoriesRemoteDatasourceImplTest {
    @Test
    fun get_categories_returns_every_category() =
        runTest {
            // given
            val client = createHttpClient(createMockEngine(responseBody = successfulCategoriesResponse), isDebug = false)
            val sut = GetCategoriesRemoteDatasourceImpl(dispatchers = TestStovetimeDispatchers(testScheduler), client = client)

            // when
            val result = sut()

            // then
            val categories = (result as NetworkResult.Success).data.categories.orEmpty()
            assertThat(categories.map { it.strCategory }).containsExactly("Beef", "Dessert").inOrder()
            assertThat(categories.first().idCategory).isEqualTo("1")
        }

    @Test
    fun get_categories_calls_the_categories_endpoint() =
        runTest {
            // given
            var requestedUrl: Url? = null
            val engine = createMockEngine(responseBody = successfulCategoriesResponse) { requestedUrl = it.url }
            val sut = GetCategoriesRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            sut()

            // then
            assertThat(requestedUrl.toString()).isEqualTo("https://www.themealdb.com/api/json/v1/1/categories.php")
        }

    @Test
    fun a_server_error_returns_a_server_error_with_its_code() =
        runTest {
            // given
            val engine = createMockEngine(status = HttpStatusCode.InternalServerError)
            val sut = GetCategoriesRemoteDatasourceImpl(TestStovetimeDispatchers(testScheduler), createHttpClient(engine, isDebug = false))

            // when
            val result = sut()

            // then
            assertThat(result).isEqualTo(NetworkResult.Error(NetworkError.Server(500)))
        }
}
