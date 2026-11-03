package com.ericwathome.stovetime.reminders

import androidx.annotation.StringRes
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.core.domain.model.WeeklyReminder

data class WeeklyReminderText(
    @param:StringRes val resId: Int,
    val formatArgs: List<Any> = emptyList(),
)

internal fun weeklyReminderText(reminder: WeeklyReminder): WeeklyReminderText {
    val oneLeft = reminder.cooksLeft == 1
    return if (reminder.weekStreak > 0) {
        WeeklyReminderText(
            resId = if (oneLeft) R.string.reminder_one_more_cook_streak else R.string.reminder_two_more_cooks_streak,
            formatArgs = listOf(reminder.weekStreak),
        )
    } else {
        WeeklyReminderText(resId = if (oneLeft) R.string.reminder_one_more_cook_goal else R.string.reminder_two_more_cooks_goal)
    }
}
