package com.ericwathome.stovetime.core.data.repo.course

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.datasources.local.helpers.LocalError
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.models.CourseCache
import com.ericwathome.stovetime.datasources.local.models.CourseUnitCache
import com.ericwathome.stovetime.datasources.local.models.LessonCache
import com.ericwathome.stovetime.datasources.local.models.LessonStepCache
import com.ericwathome.stovetime.datasources.local.models.StepTimerCache
import com.ericwathome.stovetime.datasources.local.sources.course.CourseLocalDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCourseRepositoryImplTest {
    @MockK
    private lateinit var courseLocalDatasource: CourseLocalDatasource
    private lateinit var sut: GetCourseRepositoryImpl

    private val cache =
        CourseCache(
            id = "home-basics",
            title = "Home basics",
            units =
                listOf(
                    CourseUnitCache(
                        id = "eggs",
                        number = 1,
                        title = "Eggs",
                        lessons =
                            listOf(
                                LessonCache(
                                    id = "boiled-eggs",
                                    title = "Boiled eggs",
                                    minutes = 15,
                                    difficulty = "medium",
                                    xp = 20,
                                    photo = "lesson_boiled_eggs",
                                    learn = listOf("Peeling"),
                                    ingredients = listOf("Eggs"),
                                    steps =
                                        listOf(
                                            LessonStepCache(text = "Boil"),
                                            LessonStepCache(text = "Cook", timer = StepTimerCache(540, "firm")),
                                        ),
                                ),
                            ),
                    ),
                ),
        )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        sut = GetCourseRepositoryImpl(courseLocalDatasource)
    }

    @Test
    fun the_course_maps_to_models() =
        runTest {
            // given
            coEvery { courseLocalDatasource.getCourse() } returns LocalResult.Success(cache)

            // when
            val lesson = (sut() as DataResult.Success).data.units.single().lessons.single()

            // then
            assertThat(lesson.difficulty).isEqualTo(Difficulty.Medium)
            assertThat(lesson.steps[1].timer?.seconds).isEqualTo(540)
        }

    @Test
    fun the_course_is_read_once() =
        runTest {
            // given
            coEvery { courseLocalDatasource.getCourse() } returns LocalResult.Success(cache)

            // when
            sut()
            sut()

            // then
            coVerify(exactly = 1) { courseLocalDatasource.getCourse() }
        }

    @Test
    fun a_broken_course_is_an_error() =
        runTest {
            // given
            coEvery { courseLocalDatasource.getCourse() } returns LocalResult.Error(LocalError.Unknown("bad json"))

            // when
            val result = sut()

            // then
            assertThat(result).isInstanceOf(DataResult.Error::class.java)
        }
}
