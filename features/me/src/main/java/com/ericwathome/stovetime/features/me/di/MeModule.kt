package com.ericwathome.stovetime.features.me.di

import com.ericwathome.stovetime.core.domain.di.UseCaseModule
import com.ericwathome.stovetime.features.me.home.MeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val MeViewModelModule =
    module {
        viewModelOf(::MeViewModel)
    }

val MeModule =
    module {
        includes(MeViewModelModule, UseCaseModule)
    }
