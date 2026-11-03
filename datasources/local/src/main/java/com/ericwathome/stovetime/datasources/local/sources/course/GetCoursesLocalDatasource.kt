package com.ericwathome.stovetime.datasources.local.sources.course

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.BundledContentReader
import com.ericwathome.stovetime.datasources.local.helpers.COURSE_ASSETS
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.contentJson
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.models.CourseCache

fun interface GetCoursesLocalDatasource {
    suspend operator fun invoke(): LocalResult<List<CourseCache>>
}

internal class GetCoursesLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val reader: BundledContentReader,
) : GetCoursesLocalDatasource {
    override suspend fun invoke(): LocalResult<List<CourseCache>> =
        safeDatabaseCall(dispatchers.io) {
            COURSE_ASSETS.map { asset -> contentJson.decodeFromString<CourseCache>(reader.read(asset)) }
        }
}
