package com.ericwathome.stovetime.datasources.local.sources.course

interface CourseLocalDatasource {
    val getCourses: GetCoursesLocalDatasource
}

internal class CourseLocalDatasourceImpl(
    override val getCourses: GetCoursesLocalDatasource,
) : CourseLocalDatasource
