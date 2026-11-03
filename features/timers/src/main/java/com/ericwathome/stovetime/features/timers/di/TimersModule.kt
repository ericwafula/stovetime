package com.ericwathome.stovetime.features.timers.di

import com.ericwathome.stovetime.features.timers.alarm.AlarmViewModel
import com.ericwathome.stovetime.features.timers.timers.TimersViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val TimersViewModelModule =
    module {
        viewModelOf(::TimersViewModel)
        viewModelOf(::AlarmViewModel)
    }

val TimersModule =
    module {
        includes(TimersViewModelModule)
    }
