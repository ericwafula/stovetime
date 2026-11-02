package com.ericwathome.stovetime.core.data.repo.course

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.mappers.course.toData
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.datasources.local.sources.course.CourseLocalDatasource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface GetCourseRepository {
    suspend operator fun invoke(): DataResult<Course>
}

internal class GetCourseRepositoryImpl(
    private val courseLocalDatasource: CourseLocalDatasource,
) : GetCourseRepository {
    private val mutex = Mutex()
    private var course: Course? = null

    override suspend fun invoke(): DataResult<Course> =
        mutex.withLock {
            course?.let { return@withLock DataResult.Success(it) }
            courseLocalDatasource.getCourse().mapToData { it.toData() }.also { result ->
                if (result is DataResult.Success) course = result.data
            }
        }
}
