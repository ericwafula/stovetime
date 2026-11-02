package com.ericwathome.stovetime.core.data.repo.course

interface CourseRepository {
    val getCourses: GetCoursesRepository
    val observeChosenCourse: ObserveChosenCourseRepository
    val chooseCourse: ChooseCourseRepository
}

internal class CourseRepositoryImpl(
    override val getCourses: GetCoursesRepository,
    override val observeChosenCourse: ObserveChosenCourseRepository,
    override val chooseCourse: ChooseCourseRepository,
) : CourseRepository
