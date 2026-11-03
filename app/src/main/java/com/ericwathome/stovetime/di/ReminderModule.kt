package com.ericwathome.stovetime.di

import com.ericwathome.stovetime.reminders.ScheduleWeeklyReminderWork
import com.ericwathome.stovetime.reminders.ScheduleWeeklyReminderWorkImpl
import com.ericwathome.stovetime.reminders.WeeklyReminderNotifier
import com.ericwathome.stovetime.reminders.WeeklyReminderNotifierImpl
import com.ericwathome.stovetime.reminders.WeeklyReminderWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ReminderModule =
    module {
        workerOf(::WeeklyReminderWorker)
        singleOf(::WeeklyReminderNotifierImpl) {
            bind<WeeklyReminderNotifier>()
        }
        singleOf(::ScheduleWeeklyReminderWorkImpl) {
            bind<ScheduleWeeklyReminderWork>()
        }
    }
