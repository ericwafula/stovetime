package com.ericwathome.stovetime.core.domain.model

import kotlinx.datetime.LocalDate

data class WeeklyReminder(
    val cooksLeft: Int,
    val weekStreak: Int,
    val weekStart: LocalDate,
)
