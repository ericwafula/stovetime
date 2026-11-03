package com.ericwathome.stovetime.core.data.repo.course

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.settings.CourseChoiceLocalDatasource

fun interface ChooseCourseRepository {
    suspend operator fun invoke(courseId: String): DataResult<Boolean>
}

internal class ChooseCourseRepositoryImpl(
    private val courseChoiceLocalDatasource: CourseChoiceLocalDatasource,
) : ChooseCourseRepository {
    override suspend fun invoke(courseId: String): DataResult<Boolean> = courseChoiceLocalDatasource.courseId.save(courseId).mapToData { it }
}
