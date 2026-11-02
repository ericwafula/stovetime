package com.ericwathome.stovetime.core.domain.di

import com.ericwathome.stovetime.core.domain.useCase.CalculateCookXpUseCase
import com.ericwathome.stovetime.core.domain.useCase.CalculateCookXpUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.CheckWeeklyReminderUseCase
import com.ericwathome.stovetime.core.domain.useCase.CheckWeeklyReminderUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObserveBadgesUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObserveBadgesUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObserveCoursesUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObserveCoursesUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObserveLessonPathUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObserveLessonPathUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObservePathUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObservePathUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObserveProgressUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObserveProgressUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCasesImpl
import com.ericwathome.stovetime.core.domain.useCase.ReminderUseCases
import com.ericwathome.stovetime.core.domain.useCase.ReminderUseCasesImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ProgressUseCaseModule =
    module {
        // parent
        singleOf(::ProgressUseCasesImpl) {
            bind<ProgressUseCases>()
        }
        // children
        singleOf(::ObserveProgressUseCaseImpl) {
            bind<ObserveProgressUseCase>()
        }
        singleOf(::ObservePathUseCaseImpl) {
            bind<ObservePathUseCase>()
        }
        singleOf(::ObserveLessonPathUseCaseImpl) {
            bind<ObserveLessonPathUseCase>()
        }
        singleOf(::ObserveBadgesUseCaseImpl) {
            bind<ObserveBadgesUseCase>()
        }
        singleOf(::ObserveCoursesUseCaseImpl) {
            bind<ObserveCoursesUseCase>()
        }
        singleOf(::CalculateCookXpUseCaseImpl) {
            bind<CalculateCookXpUseCase>()
        }
    }

internal val ReminderUseCaseModule =
    module {
        // parent
        singleOf(::ReminderUseCasesImpl) {
            bind<ReminderUseCases>()
        }
        // children
        singleOf(::CheckWeeklyReminderUseCaseImpl) {
            bind<CheckWeeklyReminderUseCase>()
        }
    }

val UseCaseModule =
    module {
        includes(ProgressUseCaseModule, ReminderUseCaseModule)
    }
