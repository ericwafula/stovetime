package com.ericwathome.stovetime.core.domain.di

import com.ericwathome.stovetime.core.domain.useCase.CalculateCookXpUseCase
import com.ericwathome.stovetime.core.domain.useCase.CalculateCookXpUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObservePathUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObservePathUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ObserveProgressUseCase
import com.ericwathome.stovetime.core.domain.useCase.ObserveProgressUseCaseImpl
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCasesImpl
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
        singleOf(::CalculateCookXpUseCaseImpl) {
            bind<CalculateCookXpUseCase>()
        }
    }

val UseCaseModule =
    module {
        includes(ProgressUseCaseModule)
    }
