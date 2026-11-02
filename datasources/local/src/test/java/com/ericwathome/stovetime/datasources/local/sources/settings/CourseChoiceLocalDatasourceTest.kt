package com.ericwathome.stovetime.datasources.local.sources.settings

import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.createPreferencesDataStore
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CourseChoiceLocalDatasourceTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.createSut(): CourseChoiceLocalDatasource {
        val dataStore = createPreferencesDataStore(backgroundScope) { folder.newFile("course_choice.preferences_pb").also { it.delete() } }
        return CourseChoiceLocalDatasourceImpl(dataStore, TestStovetimeDispatchers(testScheduler))
    }

    @Test
    fun no_course_is_chosen_on_a_fresh_install() =
        runTest {
            // given
            val sut = createSut()

            // then
            assertThat(sut.courseId.get()).isEqualTo(LocalResult.Success(null))
        }

    @Test
    fun a_chosen_course_is_read_back() =
        runTest {
            // given
            val sut = createSut()

            // when
            sut.courseId.save("kenyan-favourites")

            // then
            assertThat(sut.courseId.get()).isEqualTo(LocalResult.Success("kenyan-favourites"))
        }
}
