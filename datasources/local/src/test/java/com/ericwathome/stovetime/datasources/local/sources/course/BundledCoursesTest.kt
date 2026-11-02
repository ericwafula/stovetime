package com.ericwathome.stovetime.datasources.local.sources.course

import com.ericwathome.stovetime.datasources.local.helpers.COURSE_ASSETS
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.problems
import com.ericwathome.stovetime.datasources.local.models.CourseCache
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class BundledCoursesTest {
    private fun readAsset(fileName: String): String = File("src/main/assets/$fileName").readText()

    private suspend fun TestScope.loadCourses(): List<CourseCache> {
        val result = GetCoursesLocalDatasourceImpl(TestStovetimeDispatchers(testScheduler), ::readAsset)()
        assertThat(result).isInstanceOf(LocalResult.Success::class.java)
        return (result as LocalResult.Success).data
    }

    private fun CourseCache.lessonTitles() = units.flatMap { it.lessons }.map { it.title }

    @Test
    fun four_courses_load_with_home_basics_first() =
        runTest {
            // when
            val titles = loadCourses().map { it.title }

            // then
            assertThat(titles)
                .containsExactly("Home basics", "Rice & grains", "One-pan dinners", "Kenyan favourites")
                .inOrder()
        }

    @Test
    fun every_course_file_in_the_assets_is_loaded() {
        // when
        val files = File("src/main/assets/courses").list().orEmpty().map { "courses/$it" }

        // then
        assertThat(COURSE_ASSETS).containsExactlyElementsIn(files)
    }

    @Test
    fun every_course_has_one_unit_of_at_least_three_lessons() =
        runTest {
            // when
            val courses = loadCourses()

            // then
            courses.forEach { course ->
                assertThat(course.units).hasSize(1)
                assertThat(course.units.single().lessons.size).isAtLeast(3)
            }
        }

    @Test
    fun every_course_breaks_none_of_the_content_rules() =
        runTest {
            // when
            val courses = loadCourses()

            // then
            courses.forEach { assertThat(it.problems()).isEmpty() }
            assertThat(courses.problems()).isEmpty()
        }

    @Test
    fun lesson_ids_are_unique_across_courses() =
        runTest {
            // when
            val ids = loadCourses().flatMap { course -> course.units.flatMap { it.lessons } }.map { it.id }

            // then
            assertThat(ids).containsNoDuplicates()
        }

    @Test
    fun kenyan_favourites_has_pilau_chapati_and_sukuma_wiki() =
        runTest {
            // when
            val titles = loadCourses().single { it.id == "kenyan-favourites" }.lessonTitles()

            // then
            assertThat(titles).containsAtLeast("Beef pilau", "Chapati", "Sukuma wiki")
        }

    @Test
    fun a_lesson_id_shared_by_two_courses_is_a_problem() =
        runTest {
            // given
            val (first, second) = loadCourses()
            val borrowed = first.units.single().lessons.first()
            val copied =
                second.copy(
                    units = second.units.map { unit -> unit.copy(lessons = unit.lessons + borrowed.copy(title = "Same dish again")) },
                )

            // then
            assertThat(listOf(first, copied).problems()).contains("lesson id ${borrowed.id} is used in more than one course")
        }

    @Test
    fun a_broken_course_file_is_an_error_not_a_crash() =
        runTest {
            // when
            val result = GetCoursesLocalDatasourceImpl(TestStovetimeDispatchers(testScheduler)) { "{ not json" }()

            // then
            assertThat(result).isInstanceOf(LocalResult.Error::class.java)
        }
}
