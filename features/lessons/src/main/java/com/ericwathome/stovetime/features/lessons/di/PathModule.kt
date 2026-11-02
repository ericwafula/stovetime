package com.ericwathome.stovetime.features.lessons.di

import com.ericwathome.stovetime.core.domain.di.UseCaseModule
import com.ericwathome.stovetime.features.lessons.home.PathViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val PathViewModelModule =
    module {
        viewModelOf(::PathViewModel)
    }

val PathModule =
    module {
        includes(PathViewModelModule, UseCaseModule)
    }
