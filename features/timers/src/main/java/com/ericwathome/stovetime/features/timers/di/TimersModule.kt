package com.ericwathome.stovetime.features.timers.di

import com.ericwathome.stovetime.features.timers.alarm.AlarmViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val TimersViewModelModule =
    module {
        viewModelOf(::AlarmViewModel)
    }

val TimersModule =
    module {
        includes(TimersViewModelModule)
    }
