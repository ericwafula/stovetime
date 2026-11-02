package com.ericwathome.stovetime.core.domain.useCase

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.repo.course.ChooseCourseRepository
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.data.repo.course.GetCoursesRepository
import com.ericwathome.stovetime.core.data.repo.course.ObserveChosenCourseRepository
import com.ericwathome.stovetime.core.domain.model.CourseSummary
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.model.Progress
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.junit.Test
import kotlin.time.Duration.Companion.hours

class CoursePathUseCasesTest {
    private val wednesday = LocalDate(2026, 11, 4)

    private fun lesson(id: String) =
        Lesson(
            id = id,
            title = id,
            minutes = 10,
            difficulty = Difficulty.Easy,
            xp = 30,
            photo = "lesson_$id",
            learn = emptyList(),
            ingredients = emptyList(),
            steps = emptyList(),
        )

    private fun course(
        id: String,
        vararg lessonIds: String,
    ) = Course(
        id = id,
        title = id,
        units = listOf(CourseUnit(id = "$id-unit", number = 1, title = id, lessons = lessonIds.map { lesson(it) })),
    )

    private val homeBasics = course("home-basics", "boiled-eggs", "scrambled-eggs", "shakshuka")
    private val kenyan = course("kenyan-favourites", "sukuma-wiki", "ugali", "chapati", "beef-pilau")
    private val courses = listOf(homeBasics, kenyan)

    private fun cook(dishId: String) =
        Cook(
            id = 0,
            dishId = dishId,
            kind = CookKind.Lesson,
            name = dishId,
            photo = null,
            startedAt = 0,
            finishedAt = (wednesday.atStartOfDayIn(TimeZone.currentSystemDefault()) + 12.hours).toEpochMilliseconds(),
            xp = 30,
            rating = null,
            weeklyGoal = 4,
        )

    private fun progressWith(vararg cooked: String): DataResult<Progress> =
        DataResult.Success(progressOf(cooked.map { cook(it) }, goal = 4, today = wednesday))

    private val chosen = MutableStateFlow<DataResult<Course>>(DataResult.Success(homeBasics))
    private val progress = MutableStateFlow(progressWith())

    private val courseRepository =
        object : CourseRepository {
            override val getCourses = GetCoursesRepository { DataResult.Success(courses) }
            override val observeChosenCourse = ObserveChosenCourseRepository { chosen }
            override val chooseCourse =
                ChooseCourseRepository { courseId ->
                    chosen.value = DataResult.Success(courses.single { it.id == courseId })
                    DataResult.Success(true)
                }
        }

    private val observeProgress = ObserveProgressUseCase { progress }

    private fun DataResult<PathProgress>.path() = (this as DataResult.Success).data

    @Test
    fun the_path_shows_the_chosen_course() =
        runTest {
            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                val path = awaitItem().path()
                assertThat(path.courseId).isEqualTo("home-basics")
                assertThat(path.lessons.map { it.lesson.id }).containsExactly("boiled-eggs", "scrambled-eggs", "shakshuka").inOrder()
            }
        }

    @Test
    fun choosing_another_course_switches_the_path() =
        runTest {
            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                skipItems(1)

                // when
                courseRepository.chooseCourse("kenyan-favourites")

                // then
                val path = awaitItem().path()
                assertThat(path.courseTitle).isEqualTo("kenyan-favourites")
                assertThat(path.nextLesson?.lesson?.id).isEqualTo("sukuma-wiki")
            }
        }

    @Test
    fun cooking_in_one_course_never_unlocks_lessons_in_another() =
        runTest {
            // given
            progress.value = progressWith("boiled-eggs", "scrambled-eggs", "shakshuka")
            chosen.value = DataResult.Success(kenyan)

            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                val path = awaitItem().path()
                assertThat(path.lessons.map { it.state })
                    .containsExactly(LessonState.Current, LessonState.Locked, LessonState.Locked, LessonState.Locked)
                    .inOrder()
                assertThat(path.cookedCount).isEqualTo(0)
            }
        }

    @Test
    fun each_course_unlocks_its_own_lessons_in_order() =
        runTest {
            // given
            progress.value = progressWith("boiled-eggs", "sukuma-wiki", "ugali")
            chosen.value = DataResult.Success(kenyan)

            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                val path = awaitItem().path()
                assertThat(path.lessons.map { it.state })
                    .containsExactly(LessonState.Done, LessonState.Done, LessonState.Current, LessonState.Locked)
                    .inOrder()
            }
        }

    @Test
    fun xp_counts_cooks_from_every_course() =
        runTest {
            // given
            progress.value = progressWith("boiled-eggs", "sukuma-wiki")

            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                assertThat(awaitItem().path().progress.totalXp).isEqualTo(60)
            }
        }

    @Test
    fun a_course_that_fails_to_load_passes_its_error_through() =
        runTest {
            // given
            chosen.value = DataResult.Error(DataError.Unknown("bad json"))

            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                assertThat(awaitItem()).isEqualTo(DataResult.Error(DataError.Unknown("bad json")))
            }
        }

    @Test
    fun a_lesson_from_a_course_not_shown_on_the_path_resolves_in_its_own_course() =
        runTest {
            // given
            progress.value = progressWith("sukuma-wiki")

            ObserveLessonPathUseCaseImpl(courseRepository, observeProgress)("ugali").test {
                // then
                val path = awaitItem().path()
                assertThat(path.courseId).isEqualTo("kenyan-favourites")
                val lesson = path.lessons.single { it.lesson.id == "ugali" }
                assertThat(lesson.position).isEqualTo(2)
                assertThat(lesson.state).isEqualTo(LessonState.Current)
                assertThat(path.lessons).hasSize(4)
            }
        }

    @Test
    fun an_unknown_lesson_is_an_error() =
        runTest {
            ObserveLessonPathUseCaseImpl(courseRepository, observeProgress)("no-such-lesson").test {
                // then
                assertThat(awaitItem()).isInstanceOf(DataResult.Error::class.java)
                awaitComplete()
            }
        }

    @Test
    fun the_course_list_counts_cooked_lessons_and_marks_the_chosen_course() =
        runTest {
            // given
            progress.value = progressWith("boiled-eggs", "boiled-eggs", "sukuma-wiki", "ugali", "chapati")
            chosen.value = DataResult.Success(kenyan)

            ObserveCoursesUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                assertThat((awaitItem() as DataResult.Success).data)
                    .containsExactly(
                        CourseSummary(id = "home-basics", title = "home-basics", cookedCount = 1, lessonCount = 3, isChosen = false),
                        CourseSummary(id = "kenyan-favourites", title = "kenyan-favourites", cookedCount = 3, lessonCount = 4, isChosen = true),
                    ).inOrder()
            }
        }

    @Test
    fun the_course_list_follows_the_chosen_course() =
        runTest {
            ObserveCoursesUseCaseImpl(courseRepository, observeProgress)().test {
                skipItems(1)

                // when
                courseRepository.chooseCourse("kenyan-favourites")

                // then
                val summaries = (awaitItem() as DataResult.Success).data
                assertThat(summaries.single { it.isChosen }.id).isEqualTo("kenyan-favourites")
            }
        }

    @Test
    fun courses_that_fail_to_load_leave_the_course_list_as_an_error() =
        runTest {
            // given
            val broken =
                object : CourseRepository by courseRepository {
                    override val getCourses = GetCoursesRepository { DataResult.Error(DataError.Unknown("bad json")) }
                }

            ObserveCoursesUseCaseImpl(broken, { flowOf(progressWith()) })().test {
                // then
                assertThat(awaitItem()).isEqualTo(DataResult.Error(DataError.Unknown("bad json")))
                awaitComplete()
            }
        }
}
