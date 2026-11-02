package com.ericwathome.stovetime.features.lessons.home

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.core.common.result.DataError
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PathViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var progressUseCases: ProgressUseCases

    private val lessons = listOf("boiled-eggs", "scrambled-eggs", "jammy-eggs-on-toast", "french-omelette", "shakshuka")
    private val path = MutableStateFlow<DataResult<PathProgress>>(DataResult.Success(pathWith(cooked = 0)))
    private lateinit var sut: PathViewModel

    private fun pathWith(
        cooked: Int,
        xp: Int = 0,
    ): PathProgress {
        val models = lessons.map { sampleLesson(it, it) }
        return PathProgress(
            courseTitle = "Home basics",
            unit = CourseUnit(id = "eggs", number = 1, title = "Eggs", lessons = models),
            lessons =
                models.mapIndexed { index, lesson ->
                    val state =
                        when {
                            index < cooked -> LessonState.Done
                            index == cooked -> LessonState.Current
                            else -> LessonState.Locked
                        }
                    PathLesson(lesson, state, index + 1, cookedBefore = index < cooked, nextCookXp = lesson.xp)
                },
            progress = Progress(xp, 0, 0, WeekProgress(emptyList(), 0, 4), emptyList()),
        )
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { progressUseCases.observePath() } returns path
        sut = PathViewModel(progressUseCases)
    }

    @Test
    fun a_fresh_install_shows_the_first_lesson_current_and_the_rest_locked() =
        runTest {
            sut.state.test {
                // then
                val shown = (expectMostRecentItem().path as ViewState.Success).data
                assertThat(shown.lessons.map { it.state })
                    .containsExactly(LessonState.Current, LessonState.Locked, LessonState.Locked, LessonState.Locked, LessonState.Locked)
                    .inOrder()
                assertThat(shown.progress.totalXp).isEqualTo(0)
            }
        }

    @Test
    fun progress_updates_as_soon_as_a_cook_is_saved() =
        runTest {
            sut.state.test {
                skipItems(1)

                // when
                path.value = DataResult.Success(pathWith(cooked = 1, xp = 20))

                // then
                val shown = (expectMostRecentItem().path as ViewState.Success).data
                assertThat(shown.nextLesson?.lesson?.id).isEqualTo("scrambled-eggs")
                assertThat(shown.progress.totalXp).isEqualTo(20)
            }
        }

    @Test
    fun a_finished_unit_has_no_next_lesson() =
        runTest {
            // given
            path.value = DataResult.Success(pathWith(cooked = 5))

            sut.state.test {
                // then
                val shown = (expectMostRecentItem().path as ViewState.Success).data
                assertThat(shown.isUnitDone).isTrue()
                assertThat(shown.cookedCount).isEqualTo(5)
            }
        }

    @Test
    fun tapping_a_locked_node_says_so_and_opens_nothing() =
        runTest {
            sut.state.test { cancelAndIgnoreRemainingEvents() }
            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(PathAction.OnClickLesson("shakshuka"))

                    // then
                    assertThat(awaitItem()).isEqualTo(PathEvent.ShowLocked)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun tapping_start_opens_the_current_lesson() =
        runTest {
            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(PathAction.OnClickStart)

                    // then
                    assertThat(awaitItem()).isEqualTo(PathEvent.OpenLesson("boiled-eggs"))
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun tapping_a_done_node_opens_it_to_cook_again() =
        runTest {
            // given
            path.value = DataResult.Success(pathWith(cooked = 2))

            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(PathAction.OnClickLesson("boiled-eggs"))

                    // then
                    assertThat(awaitItem()).isEqualTo(PathEvent.OpenLesson("boiled-eggs"))
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun a_course_that_fails_to_load_shows_an_error_and_retry_loads_it() =
        runTest {
            // given
            path.value = DataResult.Error(DataError.Unknown("broken"))

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().path).isEqualTo(ViewState.Error(DataError.Unknown("broken")))

                // when
                path.value = DataResult.Success(pathWith(cooked = 0))
                sut.onAction(PathAction.OnClickRetry)

                // then
                assertThat(expectMostRecentItem().path).isInstanceOf(ViewState.Success::class.java)
            }
        }
}
