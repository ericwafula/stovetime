package com.ericwathome.stovetime.core.data.model.progress

enum class CookKind { Lesson, Recipe }

enum class Rating { Overdone, PrettyGood, NailedIt }

data class Cook(
    val id: Long,
    val dishId: String,
    val kind: CookKind,
    val name: String,
    val photo: String?,
    val startedAt: Long,
    val finishedAt: Long,
    val xp: Int,
    val rating: Rating?,
    val weeklyGoal: Int,
)

data class NewCook(
    val dishId: String,
    val kind: CookKind,
    val name: String,
    val photo: String?,
    val startedAt: Long,
    val finishedAt: Long,
    val xp: Int,
    val weeklyGoal: Int,
    val rating: Rating? = null,
)
