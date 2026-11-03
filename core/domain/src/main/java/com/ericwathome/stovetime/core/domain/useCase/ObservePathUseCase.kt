package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.rules.lessonStates
import com.ericwathome.stovetime.core.domain.rules.lessonXp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun interface ObservePathUseCase {
    operator fun invoke(): Flow<DataResult<PathProgress>>
}

internal class ObservePathUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val observeProgress: ObserveProgressUseCase,
) : ObservePathUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<DataResult<PathProgress>> =
        courseRepository.observeChosenCourse().flatMapLatest { course ->
            when (course) {
                is DataResult.Error -> flowOf(course)
                is DataResult.Success -> observeProgress().map { result -> result.withPath(course.data, course.data.units.first()) }
            }
        }
}

internal fun DataResult<Progress>.withPath(
    course: Course,
    unit: CourseUnit,
): DataResult<PathProgress> =
    when (this) {
        is DataResult.Error -> this
        is DataResult.Success -> DataResult.Success(pathOf(course, data, unit))
    }

internal fun cookedLessonIds(progress: Progress): Set<String> =
    progress.history
        .filter { it.kind == CookKind.Lesson }
        .map { it.dishId }
        .toSet()

internal fun pathOf(
    course: Course,
    progress: Progress,
    unit: CourseUnit = course.units.first(),
): PathProgress {
    val cookedLessonIds = cookedLessonIds(progress)
    val states = lessonStates(unit.lessons.map { it.id }, cookedLessonIds)
    return PathProgress(
        courseId = course.id,
        courseTitle = course.title,
        unit = unit,
        lessons =
            unit.lessons.mapIndexed { index, lesson ->
                val cookedBefore = lesson.id in cookedLessonIds
                PathLesson(
                    lesson = lesson,
                    state = states[index],
                    position = index + 1,
                    cookedBefore = cookedBefore,
                    nextCookXp = lessonXp(lessonXp = lesson.xp, cookedBefore = cookedBefore),
                )
            },
        progress = progress,
    )
}
