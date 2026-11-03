package com.ericwathome.stovetime.datasources.local.sources.course

import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.problems
import com.ericwathome.stovetime.datasources.local.models.CourseCache
import com.ericwathome.stovetime.testing.TestStovetimeDispatchers
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class HomeBasicsCourseTest {
    private fun readAsset(fileName: String): String = File("src/main/assets/$fileName").readText()

    private suspend fun kotlinx.coroutines.test.TestScope.loadCourse(): CourseCache {
        val result = GetCoursesLocalDatasourceImpl(TestStovetimeDispatchers(testScheduler), ::readAsset)()
        assertThat(result).isInstanceOf(LocalResult.Success::class.java)
        return (result as LocalResult.Success).data.single { it.id == "home-basics" }
    }

    @Test
    fun the_course_loads_with_its_unit_and_five_lessons() =
        runTest {
            // when
            val course = loadCourse()

            // then
            assertThat(course.title).isEqualTo("Home basics")
            assertThat(course.units.single().title).isEqualTo("Crack it: five ways with eggs")
            assertThat(course.units.single().lessons).hasSize(5)
        }

    @Test
    fun the_lessons_are_in_order() =
        runTest {
            // when
            val titles = loadCourse().units.single().lessons.map { it.title }

            // then
            assertThat(titles)
                .containsExactly("Boiled eggs", "Scrambled eggs", "Jammy eggs on toast", "French omelette", "Shakshuka")
                .inOrder()
        }

    @Test
    fun the_course_breaks_none_of_the_content_rules() =
        runTest {
            // then
            assertThat(loadCourse().problems()).isEmpty()
        }

    @Test
    fun every_timed_step_says_what_done_looks_like() =
        runTest {
            // when
            val timers = loadCourse().units.flatMap { it.lessons }.flatMap { it.steps }.mapNotNull { it.timer }

            // then
            assertThat(timers).isNotEmpty()
            assertThat(timers.all { it.doneness.isNotBlank() }).isTrue()
        }

    @Test
    fun lesson_ids_are_unique() =
        runTest {
            // when
            val ids = loadCourse().units.flatMap { it.lessons }.map { it.id }

            // then
            assertThat(ids).containsNoDuplicates()
        }

    @Test
    fun jammy_eggs_on_toast_matches_the_design() =
        runTest {
            // when
            val lesson = loadCourse().units.single().lessons.single { it.id == "jammy-eggs-on-toast" }

            // then
            assertThat(lesson.minutes).isEqualTo(15)
            assertThat(lesson.difficulty).isEqualTo("easy")
            assertThat(lesson.xp).isEqualTo(30)
            assertThat(lesson.steps).hasSize(5)
            assertThat(lesson.steps[1].timer?.seconds).isEqualTo(390)
        }

    @Test
    fun every_lesson_photo_is_bundled() =
        runTest {
            // when
            val photos = loadCourse().units.flatMap { it.lessons }.map { it.photo }

            // then
            photos.forEach { assertThat(File("src/main/res/drawable-nodpi/$it.webp").exists()).isTrue() }
        }

    @Test
    fun a_timer_without_a_doneness_check_is_a_problem() =
        runTest {
            // given
            val course = loadCourse()
            val broken =
                course.copy(
                    units =
                        course.units.map { unit ->
                            unit.copy(
                                lessons =
                                    unit.lessons.map { lesson ->
                                        lesson.copy(steps = lesson.steps.map { step -> step.copy(timer = step.timer?.copy(doneness = "")) })
                                    },
                            )
                        },
                )

            // then
            assertThat(broken.problems()).isNotEmpty()
        }

    @Test
    fun a_step_with_a_tip_but_no_timer_is_valid() =
        runTest {
            // given
            val course = loadCourse()
            val lesson = course.units.single().lessons.first()
            val withTip = lesson.copy(steps = lesson.steps + lesson.steps.first().copy(timer = null, tip = "Use a timer"))

            // then
            assertThat(course.copy(units = listOf(course.units.single().copy(lessons = listOf(withTip)))).problems()).isEmpty()
        }
}
