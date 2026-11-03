package com.ericwathome.stovetime.core.data.repo.course

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.mappers.course.toData
import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.datasources.local.sources.course.CourseLocalDatasource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface GetCoursesRepository {
    suspend operator fun invoke(): DataResult<List<Course>>
}

internal class GetCoursesRepositoryImpl(
    private val courseLocalDatasource: CourseLocalDatasource,
) : GetCoursesRepository {
    private val mutex = Mutex()
    private var courses: List<Course>? = null

    override suspend fun invoke(): DataResult<List<Course>> =
        mutex.withLock {
            courses?.let { return@withLock DataResult.Success(it) }
            courseLocalDatasource.getCourses().mapToData { caches -> caches.map { it.toData() } }.also { result ->
                if (result is DataResult.Success) courses = result.data
            }
        }
}
