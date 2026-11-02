package com.ericwathome.stovetime.core.domain.useCase

interface ReminderUseCases {
    val checkWeeklyReminder: CheckWeeklyReminderUseCase
}

internal class ReminderUseCasesImpl(
    override val checkWeeklyReminder: CheckWeeklyReminderUseCase,
) : ReminderUseCases
