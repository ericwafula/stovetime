package com.ericwathome.stovetime.features.lessons.di

import com.ericwathome.stovetime.core.domain.di.UseCaseModule
import com.ericwathome.stovetime.features.lessons.cook.CookViewModel
import com.ericwathome.stovetime.features.lessons.finish.FinishViewModel
import com.ericwathome.stovetime.features.lessons.home.PathViewModel
import com.ericwathome.stovetime.features.lessons.intro.LessonIntroViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val LessonsViewModelModule =
    module {
        viewModelOf(::PathViewModel)
        viewModelOf(::LessonIntroViewModel)
        viewModelOf(::CookViewModel)
        viewModelOf(::FinishViewModel)
    }

val LessonsModule =
    module {
        includes(LessonsViewModelModule, UseCaseModule)
    }
