package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.rules.lessonStates
import com.ericwathome.stovetime.core.domain.rules.lessonXp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun interface ObservePathUseCase {
    operator fun invoke(): Flow<DataResult<PathProgress>>
}

internal class ObservePathUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val observeProgress: ObserveProgressUseCase,
) : ObservePathUseCase {
    override fun invoke(): Flow<DataResult<PathProgress>> =
        flow {
            when (val course = courseRepository.getCourse()) {
                is DataResult.Error -> {
                    emit(course)
                }

                is DataResult.Success -> {
                    observeProgress().map { result ->
                        when (result) {
                            is DataResult.Error -> result
                            is DataResult.Success -> DataResult.Success(pathOf(course.data, result.data))
                        }
                    }.collect { emit(it) }
                }
            }
        }
}

internal fun pathOf(
    course: Course,
    progress: Progress,
): PathProgress {
    val unit = course.units.first()
    val cookedLessonIds = progress.history.filter { it.kind == CookKind.Lesson }.map { it.dishId }.toSet()
    val states = lessonStates(unit.lessons.map { it.id }, cookedLessonIds)
    return PathProgress(
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
