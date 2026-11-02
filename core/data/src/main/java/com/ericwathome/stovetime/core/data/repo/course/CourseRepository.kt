package com.ericwathome.stovetime.core.data.repo.course

interface CourseRepository {
    val getCourse: GetCourseRepository
}

internal class CourseRepositoryImpl(
    override val getCourse: GetCourseRepository,
) : CourseRepository
