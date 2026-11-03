package com.ericwathome.stovetime.datasources.local.helpers

import androidx.datastore.preferences.core.intPreferencesKey
import app.cash.turbine.test
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PreferenceStoreTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.createStore(): PreferenceStore<Int> {
        val dataStore = createPreferencesDataStore(backgroundScope) { folder.newFile("settings.preferences_pb").also { it.delete() } }
        return dataStore.preference(intPreferencesKey("weekly_goal"), TestStovetimeDispatchers(testScheduler))
    }

    @Test
    fun a_fresh_store_has_no_value() =
        runTest {
            // given
            val sut = createStore()

            // when
            val result = sut.get()

            // then
            assertThat(result).isEqualTo(LocalResult.Success(null))
        }

    @Test
    fun a_saved_value_is_seen_straight_away() =
        runTest {
            // given
            val sut = createStore()

            sut.observe().test {
                assertThat(awaitItem()).isNull()

                // when
                sut.save(6)

                // then
                assertThat(awaitItem()).isEqualTo(6)
            }
        }

    @Test
    fun delete_removes_the_value() =
        runTest {
            // given
            val sut = createStore()
            sut.save(3)

            // when
            sut.delete()

            // then
            assertThat(sut.get()).isEqualTo(LocalResult.Success(null))
        }
}
