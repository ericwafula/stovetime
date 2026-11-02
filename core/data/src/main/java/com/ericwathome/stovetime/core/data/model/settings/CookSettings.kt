package com.ericwathome.stovetime.core.data.model.settings

data class CookSettings(
    val weeklyGoal: Int = DEFAULT_WEEKLY_GOAL,
    val keepScreenOn: Boolean = true,
) {
    companion object {
        const val DEFAULT_WEEKLY_GOAL = 4
        const val MIN_WEEKLY_GOAL = 1
        const val MAX_WEEKLY_GOAL = 7
    }
}
