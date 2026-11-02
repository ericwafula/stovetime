package com.ericwathome.stovetime.datasources.local.models

import kotlinx.serialization.Serializable

@Serializable
data class CourseCache(
    val id: String,
    val title: String,
    val units: List<CourseUnitCache>,
)

@Serializable
data class CourseUnitCache(
    val id: String,
    val number: Int,
    val title: String,
    val lessons: List<LessonCache>,
)

@Serializable
data class LessonCache(
    val id: String,
    val title: String,
    val minutes: Int,
    val difficulty: String,
    val xp: Int,
    val photo: String,
    val learn: List<String>,
    val ingredients: List<String>,
    val steps: List<LessonStepCache>,
)

@Serializable
data class LessonStepCache(
    val text: String,
    val detail: String? = null,
    val timer: StepTimerCache? = null,
    val tip: String? = null,
)

@Serializable
data class StepTimerCache(
    val seconds: Int,
    val doneness: String,
)
