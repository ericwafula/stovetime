package com.ericwathome.stovetime.core.domain.model

import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.data.model.progress.Cook
import kotlinx.datetime.LocalDate

enum class LessonState { Done, Current, Locked }

data class WeekDay(
    val date: LocalDate,
    val cooked: Boolean,
    val isToday: Boolean,
)

data class WeekProgress(
    val days: List<WeekDay>,
    val cooks: Int,
    val goal: Int,
) {
    val isGoalMet: Boolean
        get() = cooks >= goal
}

data class Progress(
    val totalXp: Int,
    val weekStreak: Int,
    val bestWeekStreak: Int,
    val thisWeek: WeekProgress,
    val history: List<Cook>,
) {
    val cookCount: Int
        get() = history.size
}

data class PathLesson(
    val lesson: Lesson,
    val state: LessonState,
    val position: Int,
    val cookedBefore: Boolean,
)

data class PathProgress(
    val courseTitle: String,
    val unit: CourseUnit,
    val lessons: List<PathLesson>,
    val progress: Progress,
) {
    val cookedCount: Int
        get() = lessons.count { it.state == LessonState.Done }

    val nextLesson: PathLesson?
        get() = lessons.firstOrNull { it.state == LessonState.Current }

    val isUnitDone: Boolean
        get() = nextLesson == null
}
