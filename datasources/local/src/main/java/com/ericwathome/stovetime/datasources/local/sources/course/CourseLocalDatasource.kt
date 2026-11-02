package com.ericwathome.stovetime.datasources.local.sources.course

interface CourseLocalDatasource {
    val getCourse: GetCourseLocalDatasource
}

internal class CourseLocalDatasourceImpl(
    override val getCourse: GetCourseLocalDatasource,
) : CourseLocalDatasource
