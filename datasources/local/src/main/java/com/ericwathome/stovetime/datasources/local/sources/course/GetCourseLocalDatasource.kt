package com.ericwathome.stovetime.datasources.local.sources.course

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.helpers.BundledContentReader
import com.ericwathome.stovetime.datasources.local.helpers.HOME_BASICS_COURSE_ASSET
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.helpers.contentJson
import com.ericwathome.stovetime.datasources.local.helpers.safeDatabaseCall
import com.ericwathome.stovetime.datasources.local.models.CourseCache

fun interface GetCourseLocalDatasource {
    suspend operator fun invoke(): LocalResult<CourseCache>
}

internal class GetCourseLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val reader: BundledContentReader,
) : GetCourseLocalDatasource {
    override suspend fun invoke(): LocalResult<CourseCache> =
        safeDatabaseCall(dispatchers.io) {
            contentJson.decodeFromString<CourseCache>(reader.read(HOME_BASICS_COURSE_ASSET))
        }
}
