package com.ericwathome.stovetime.core.data.mappers.course

import com.ericwathome.stovetime.core.data.model.course.Course
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.data.model.course.LessonStep
import com.ericwathome.stovetime.core.data.model.course.StepTimer
import com.ericwathome.stovetime.datasources.local.models.CourseCache
import com.ericwathome.stovetime.datasources.local.models.CourseUnitCache
import com.ericwathome.stovetime.datasources.local.models.LessonCache
import com.ericwathome.stovetime.datasources.local.models.LessonStepCache

internal fun CourseCache.toData(): Course = Course(id = id, title = title, units = units.map { it.toData() })

internal fun CourseUnitCache.toData(): CourseUnit = CourseUnit(id = id, number = number, title = title, lessons = lessons.map { it.toData() })

internal fun LessonCache.toData(): Lesson =
    Lesson(
        id = id,
        title = title,
        minutes = minutes,
        difficulty = Difficulty.entries.firstOrNull { it.name.equals(difficulty, ignoreCase = true) } ?: Difficulty.Easy,
        xp = xp,
        photo = photo,
        learn = learn,
        ingredients = ingredients,
        steps = steps.map { it.toData() },
    )

internal fun LessonStepCache.toData(): LessonStep =
    LessonStep(
        text = text,
        detail = detail,
        timer = timer?.let { StepTimer(seconds = it.seconds, doneness = it.doneness) },
        tip = tip,
    )
