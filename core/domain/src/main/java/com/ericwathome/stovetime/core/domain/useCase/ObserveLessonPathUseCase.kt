package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.domain.model.PathProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun interface ObserveLessonPathUseCase {
    operator fun invoke(lessonId: String): Flow<DataResult<PathProgress>>
}

internal class ObserveLessonPathUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val observeProgress: ObserveProgressUseCase,
) : ObserveLessonPathUseCase {
    override fun invoke(lessonId: String): Flow<DataResult<PathProgress>> =
        flow {
            when (val courses = courseRepository.getCourses()) {
                is DataResult.Error -> {
                    emit(courses)
                }

                is DataResult.Success -> {
                    val place =
                        courses.data.firstNotNullOfOrNull { course ->
                            course.units.firstOrNull { unit -> unit.lessons.any { it.id == lessonId } }?.let { course to it }
                        }
                    if (place == null) {
                        emit(DataResult.Error(DataError.Unknown("No lesson $lessonId")))
                    } else {
                        val (course, unit) = place
                        emitAll(observeProgress().map { it.withPath(course, unit) })
                    }
                }
            }
        }
}
