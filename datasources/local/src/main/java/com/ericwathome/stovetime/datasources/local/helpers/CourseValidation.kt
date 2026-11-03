package com.ericwathome.stovetime.datasources.local.helpers

import com.ericwathome.stovetime.datasources.local.models.CourseCache

private val difficulties = setOf("easy", "medium", "hard")

internal fun CourseCache.problems(): List<String> {
    val lessons = units.flatMap { it.lessons }
    val duplicateIds = lessons.groupBy { it.id }.filterValues { it.size > 1 }.keys
    return buildList {
        if (units.isEmpty()) add("$id has no units")
        duplicateIds.forEach { add("lesson id $it is used more than once") }
        units.forEach { unit ->
            if (unit.lessons.isEmpty()) add("unit ${unit.id} has no lessons")
        }
        lessons.forEach { lesson ->
            if (lesson.title.isBlank()) add("${lesson.id} has no title")
            if (lesson.photo.isBlank()) add("${lesson.id} has no photo")
            if (lesson.minutes <= 0) add("${lesson.id} has no time")
            if (lesson.xp <= 0) add("${lesson.id} has no XP")
            if (lesson.difficulty !in difficulties) add("${lesson.id} has an unknown difficulty ${lesson.difficulty}")
            if (lesson.learn.isEmpty()) add("${lesson.id} has nothing to learn")
            if (lesson.ingredients.isEmpty()) add("${lesson.id} has no ingredients")
            if (lesson.steps.size < 2) add("${lesson.id} has fewer than two steps")
            lesson.steps.forEachIndexed { index, step ->
                val timer = step.timer ?: return@forEachIndexed
                if (timer.seconds <= 0) add("${lesson.id} step ${index + 1} has an empty timer")
                if (timer.doneness.isBlank()) add("${lesson.id} step ${index + 1} has a timer without a doneness check")
            }
        }
    }
}

internal fun List<CourseCache>.problems(): List<String> {
    val duplicateCourseIds = groupBy { it.id }.filterValues { it.size > 1 }.keys
    val sharedLessonIds =
        flatMap { course -> course.units.flatMap { it.lessons }.map { it.id }.distinct() }
            .groupBy { it }
            .filterValues { it.size > 1 }
            .keys
    return buildList {
        duplicateCourseIds.forEach { add("course id $it is used more than once") }
        sharedLessonIds.forEach { add("lesson id $it is used in more than one course") }
        this@problems.forEach { addAll(it.problems()) }
    }
}
