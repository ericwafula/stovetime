package com.ericwathome.stovetime.core.data.repo.course

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.course.HOME_BASICS_COURSE_ID
import com.ericwathome.stovetime.datasources.local.sources.settings.CourseChoiceLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun interface ObserveChosenCourseRepository {
    operator fun invoke(): Flow<DataResult<Course>>
}

internal class ObserveChosenCourseRepositoryImpl(
    private val getCourses: GetCoursesRepository,
    private val courseChoiceLocalDatasource: CourseChoiceLocalDatasource,
) : ObserveChosenCourseRepository {
    override fun invoke(): Flow<DataResult<Course>> =
        flow {
            when (val courses = getCourses()) {
                is DataResult.Error -> {
                    emit(courses)
                }

                is DataResult.Success -> {
                    emitAll(
                        courseChoiceLocalDatasource.courseId
                            .observe()
                            .map { courseId -> courses.data.chosen(courseId) }
                            .distinctUntilChanged(),
                    )
                }
            }
        }
}

internal fun List<Course>.chosen(courseId: String?): DataResult<Course> {
    val course =
        firstOrNull { it.id == courseId }
            ?: firstOrNull { it.id == HOME_BASICS_COURSE_ID }
            ?: firstOrNull()
    return course?.let { DataResult.Success(it) } ?: DataResult.Error(DataError.Unknown("No courses"))
}
