package com.ericwathome.stovetime.core.data.repo.course

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.PreferenceStore
import com.ericwathome.stovetime.datasources.local.sources.settings.CourseChoiceLocalDatasource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChosenCourseRepositoryTest {
    private class FakePreferenceStore(
        val value: MutableStateFlow<String?> = MutableStateFlow(null),
    ) : PreferenceStore<String> {
        override suspend fun save(value: String): LocalResult<Boolean> {
            this.value.value = value
            return LocalResult.Success(true)
        }

        override suspend fun get(): LocalResult<String?> = LocalResult.Success(value.value)

        override fun observe(): Flow<String?> = value

        override suspend fun delete(): LocalResult<Boolean> {
            value.value = null
            return LocalResult.Success(true)
        }
    }

    private class FakeCourseChoice(
        override val courseId: FakePreferenceStore = FakePreferenceStore(),
    ) : CourseChoiceLocalDatasource

    private val courses =
        listOf(
            Course(id = "home-basics", title = "Home basics", units = emptyList()),
            Course(id = "rice-and-grains", title = "Rice & grains", units = emptyList()),
            Course(id = "kenyan-favourites", title = "Kenyan favourites", units = emptyList()),
        )

    private fun observe(choice: CourseChoiceLocalDatasource) = ObserveChosenCourseRepositoryImpl({ DataResult.Success(courses) }, choice)

    private fun DataResult<Course>.courseId() = (this as DataResult.Success).data.id

    @Test
    fun a_fresh_install_shows_home_basics() =
        runTest {
            observe(FakeCourseChoice())().test {
                // then
                assertThat(awaitItem().courseId()).isEqualTo("home-basics")
            }
        }

    @Test
    fun choosing_a_course_switches_to_it() =
        runTest {
            // given
            val choice = FakeCourseChoice()

            observe(choice)().test {
                assertThat(awaitItem().courseId()).isEqualTo("home-basics")

                // when
                ChooseCourseRepositoryImpl(choice)("kenyan-favourites")

                // then
                assertThat(awaitItem().courseId()).isEqualTo("kenyan-favourites")
            }
        }

    @Test
    fun the_chosen_course_is_remembered_by_a_new_repository() =
        runTest {
            // given
            val saved = FakePreferenceStore()
            ChooseCourseRepositoryImpl(FakeCourseChoice(saved))("rice-and-grains")

            observe(FakeCourseChoice(FakePreferenceStore(MutableStateFlow(saved.value.value))))().test {
                // then
                assertThat(awaitItem().courseId()).isEqualTo("rice-and-grains")
            }
        }

    @Test
    fun a_stored_course_that_no_longer_exists_falls_back_to_home_basics() =
        runTest {
            observe(FakeCourseChoice(FakePreferenceStore(MutableStateFlow("pastry-school"))))().test {
                // then
                assertThat(awaitItem().courseId()).isEqualTo("home-basics")
            }
        }

    @Test
    fun courses_that_fail_to_load_pass_their_error_through() =
        runTest {
            ObserveChosenCourseRepositoryImpl({ DataResult.Error(DataError.Unknown("bad json")) }, FakeCourseChoice())().test {
                // then
                assertThat(awaitItem()).isEqualTo(DataResult.Error(DataError.Unknown("bad json")))
                awaitComplete()
            }
        }

    @Test
    fun no_courses_at_all_is_an_error() {
        // then
        assertThat(emptyList<Course>().chosen("home-basics")).isInstanceOf(DataResult.Error::class.java)
    }
}
