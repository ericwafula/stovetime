package com.ericwathome.stovetime.features.lessons.intro

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.features.lessons.home.sampleLesson
import com.ericwathome.stovetime.testing.MainDispatcherRule
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LessonIntroViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var progressUseCases: ProgressUseCases
    private lateinit var sut: LessonIntroViewModel

    private fun path(cookedBefore: Boolean): PathProgress {
        val lessons = listOf(sampleLesson("boiled-eggs", "Boiled eggs", xp = 20), sampleLesson("scrambled-eggs", "Scrambled eggs"))
        return PathProgress(
            courseId = "home-basics",
            courseTitle = "Home basics",
            unit = CourseUnit("eggs", 1, "Eggs", lessons),
            lessons =
                listOf(
                    PathLesson(lessons[0], LessonState.Done, 1, cookedBefore = true, nextCookXp = 10),
                    PathLesson(lessons[1], LessonState.Current, 2, cookedBefore = cookedBefore, nextCookXp = if (cookedBefore) 10 else 30),
                ),
            progress = Progress(20, 0, 0, WeekProgress(emptyList(), 1, 4), emptyList()),
        )
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { progressUseCases.observeLessonPath(any()) } returns flowOf(DataResult.Success(path(cookedBefore = false)))
        sut = LessonIntroViewModel("scrambled-eggs", progressUseCases)
    }

    @Test
    fun the_intro_shows_the_lesson_and_its_place_in_the_unit() =
        runTest {
            sut.state.test {
                // then
                val intro = (expectMostRecentItem().intro as ViewState.Success).data
                assertThat(intro.pathLesson.lesson.title).isEqualTo("Scrambled eggs")
                assertThat(intro.pathLesson.position).isEqualTo(2)
                assertThat(intro.unitSize).isEqualTo(2)
                assertThat(intro.pathLesson.nextCookXp).isEqualTo(30)
            }
        }

    @Test
    fun a_lesson_cooked_before_offers_practice_xp() =
        runTest {
            // given
            every { progressUseCases.observeLessonPath(any()) } returns flowOf(DataResult.Success(path(cookedBefore = true)))
            sut = LessonIntroViewModel("scrambled-eggs", progressUseCases)

            sut.state.test {
                // then
                val intro = (expectMostRecentItem().intro as ViewState.Success).data
                assertThat(intro.pathLesson.cookedBefore).isTrue()
                assertThat(intro.pathLesson.nextCookXp).isEqualTo(10)
            }
        }

    @Test
    fun ticking_an_ingredient_twice_unticks_it() =
        runTest {
            sut.state.test {
                skipItems(1)

                // when
                sut.onAction(LessonIntroAction.OnToggleIngredient(1))

                // then
                assertThat(expectMostRecentItem().ticked).containsExactly(1)

                // when
                sut.onAction(LessonIntroAction.OnToggleIngredient(1))

                // then
                assertThat(expectMostRecentItem().ticked).isEmpty()
            }
        }

    @Test
    fun start_cooking_opens_cook_mode_and_records_the_start_time() =
        runTest {
            // given
            val before = System.currentTimeMillis()

            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(LessonIntroAction.OnClickStartCooking)

                    // then
                    val event = awaitItem() as LessonIntroEvent.OpenCook
                    assertThat(event.lessonId).isEqualTo("scrambled-eggs")
                    assertThat(event.startedAt).isAtLeast(before)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun start_cooking_works_with_nothing_ticked() =
        runTest {
            sut.state.test {
                sut.event.test {
                    // when
                    sut.onAction(LessonIntroAction.OnClickStartCooking)

                    // then
                    assertThat(awaitItem()).isInstanceOf(LessonIntroEvent.OpenCook::class.java)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun close_goes_back() =
        runTest {
            sut.event.test {
                // when
                sut.onAction(LessonIntroAction.OnClickClose)

                // then
                assertThat(awaitItem()).isEqualTo(LessonIntroEvent.Close)
            }
        }

    @Test
    fun an_unknown_lesson_shows_an_error() =
        runTest {
            // given
            sut = LessonIntroViewModel("no-such-lesson", progressUseCases)

            sut.state.test {
                // then
                assertThat(expectMostRecentItem().intro).isInstanceOf(ViewState.Error::class.java)
            }
        }
}
