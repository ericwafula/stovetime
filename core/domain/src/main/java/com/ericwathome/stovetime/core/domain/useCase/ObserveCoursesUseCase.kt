package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.domain.model.CourseSummary
import com.ericwathome.stovetime.core.domain.model.Progress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun interface ObserveCoursesUseCase {
    operator fun invoke(): Flow<DataResult<List<CourseSummary>>>
}

internal class ObserveCoursesUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val observeProgress: ObserveProgressUseCase,
) : ObserveCoursesUseCase {
    override fun invoke(): Flow<DataResult<List<CourseSummary>>> =
        flow {
            when (val courses = courseRepository.getCourses()) {
                is DataResult.Error -> {
                    emit(courses)
                }

                is DataResult.Success -> {
                    emitAll(
                        combine(courseRepository.observeChosenCourse(), observeProgress()) { chosen, progress ->
                            when {
                                chosen is DataResult.Error -> chosen
                                progress is DataResult.Error -> progress
                                else -> {
                                    val chosenId = (chosen as DataResult.Success).data.id
                                    DataResult.Success(courseSummaries(courses.data, chosenId, (progress as DataResult.Success).data))
                                }
                            }
                        },
                    )
                }
            }
        }
}

internal fun courseSummaries(
    courses: List<Course>,
    chosenId: String,
    progress: Progress,
): List<CourseSummary> {
    val cooked = cookedLessonIds(progress)
    return courses.map { course ->
        val lessonIds = course.units.flatMap { unit -> unit.lessons.map { it.id } }
        CourseSummary(
            id = course.id,
            title = course.title,
            cookedCount = lessonIds.count { it in cooked },
            lessonCount = lessonIds.size,
            isChosen = course.id == chosenId,
        )
    }
}
