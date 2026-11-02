package com.ericwathome.stovetime.core.data.model.course

data class Course(
    val id: String,
    val title: String,
    val units: List<CourseUnit>,
)

data class CourseUnit(
    val id: String,
    val number: Int,
    val title: String,
    val lessons: List<Lesson>,
)

enum class Difficulty { Easy, Medium, Hard }

data class Lesson(
    val id: String,
    val title: String,
    val minutes: Int,
    val difficulty: Difficulty,
    val xp: Int,
    val photo: String,
    val learn: List<String>,
    val ingredients: List<String>,
    val steps: List<LessonStep>,
)

data class LessonStep(
    val text: String,
    val detail: String?,
    val timer: StepTimer?,
    val tip: String?,
)

data class StepTimer(
    val seconds: Int,
    val doneness: String?,
)

const val HOME_BASICS_COURSE_ID = "home-basics"
