package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import org.junit.Test
import kotlin.time.Duration.Companion.hours

class ProgressUseCasesTest {
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

    private val course =
        Course(
            id = "home-basics",
            title = "Home basics",
            units = listOf(CourseUnit(id = "eggs", number = 1, title = "Eggs", lessons = listOf(lesson("a"), lesson("b"), lesson("c")))),
        )

    private fun cook(
        dishId: String,
        xp: Int,
        kind: CookKind = CookKind.Lesson,
    ) = Cook(
        id = 0,
        dishId = dishId,
        kind = kind,
        name = dishId,
        photo = null,
        startedAt = 0,
        finishedAt = (wednesday.atStartOfDayIn(TimeZone.currentSystemDefault()) + 12.hours).toEpochMilliseconds(),
        xp = xp,
        rating = null,
        weeklyGoal = 4,
    )

    @Test
    fun total_xp_is_the_sum_of_every_cook() {
        // when
        val progress = progressOf(listOf(cook("a", 30), cook("a", 10), cook("52772", 10, CookKind.Recipe)), goal = 4, today = wednesday)

        // then
        assertThat(progress.totalXp).isEqualTo(50)
        assertThat(progress.cookCount).isEqualTo(3)
        assertThat(progress.thisWeek.cooks).isEqualTo(3)
    }

    @Test
    fun a_fresh_install_shows_zero_everywhere() {
        // when
        val progress = progressOf(emptyList(), goal = 4, today = wednesday)

        // then
        assertThat(progress.totalXp).isEqualTo(0)
        assertThat(progress.weekStreak).isEqualTo(0)
        assertThat(progress.thisWeek.goal).isEqualTo(4)
    }

    @Test
    fun mid_unit_progress_marks_cooked_lessons_done_and_the_next_current() {
        // when
        val path = pathOf(course, progressOf(listOf(cook("a", 30)), goal = 4, today = wednesday))

        // then
        assertThat(path.lessons.map { it.state }).containsExactly(LessonState.Done, LessonState.Current, LessonState.Locked).inOrder()
        assertThat(path.nextLesson?.lesson?.id).isEqualTo("b")
        assertThat(path.cookedCount).isEqualTo(1)
    }

    @Test
    fun a_recipe_cook_does_not_unlock_a_lesson() {
        // when
        val path = pathOf(course, progressOf(listOf(cook("a", 10, CookKind.Recipe)), goal = 4, today = wednesday))

        // then
        assertThat(path.nextLesson?.lesson?.id).isEqualTo("a")
    }

    @Test
    fun recipes_always_earn_ten_xp() {
        // then
        assertThat(CalculateCookXpUseCaseImpl()(CookKind.Recipe, lessonXp = 0, cookedBefore = false)).isEqualTo(10)
        assertThat(CalculateCookXpUseCaseImpl()(CookKind.Lesson, lessonXp = 40, cookedBefore = false)).isEqualTo(40)
        assertThat(CalculateCookXpUseCaseImpl()(CookKind.Lesson, lessonXp = 40, cookedBefore = true)).isEqualTo(10)
    }

    private fun cookAt(dateTime: LocalDateTime) =
        cook("a", 30).copy(finishedAt = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds())

    @Test
    fun a_cook_at_23_59_on_sunday_counts_for_that_week() {
        // given
        val sunday = LocalDate(2026, 11, 8)

        // when
        val progress = progressOf(listOf(cookAt(LocalDateTime(2026, 11, 8, 23, 59))), goal = 1, today = sunday)

        // then
        assertThat(progress.thisWeek.cooks).isEqualTo(1)
        assertThat(progress.weekStreak).isEqualTo(1)
    }

    @Test
    fun a_cook_at_00_00_on_monday_counts_for_the_new_week() {
        // given
        val monday = LocalDate(2026, 11, 9)

        // when
        val progress = progressOf(listOf(cookAt(LocalDateTime(2026, 11, 9, 0, 0))), goal = 1, today = monday)

        // then
        assertThat(progress.thisWeek.days.first().cooked).isTrue()
        assertThat(progress.thisWeek.cooks).isEqualTo(1)
    }

    @Test
    fun the_history_lists_the_newest_cook_first() {
        // given
        val monday = cookAt(LocalDateTime(2026, 11, 2, 18, 0)).copy(id = 1)
        val wednesdayCook = cookAt(LocalDateTime(2026, 11, 4, 12, 0)).copy(id = 2)
        val tuesday = cookAt(LocalDateTime(2026, 11, 3, 9, 0)).copy(id = 3)

        // when
        val progress = progressOf(listOf(monday, wednesdayCook, tuesday), goal = 4, today = wednesday)

        // then
        assertThat(progress.history.map { it.id }).containsExactly(2L, 3L, 1L).inOrder()
    }
}
