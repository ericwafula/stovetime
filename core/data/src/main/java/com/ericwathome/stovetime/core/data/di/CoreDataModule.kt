package com.ericwathome.stovetime.core.data.di

import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.data.repo.course.CourseRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.course.GetCourseRepository
import com.ericwathome.stovetime.core.data.repo.course.GetCourseRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.progress.ClearProgressRepository
import com.ericwathome.stovetime.core.data.repo.progress.ClearProgressRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.progress.ObserveCooksRepository
import com.ericwathome.stovetime.core.data.repo.progress.ObserveCooksRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.progress.ProgressRepository
import com.ericwathome.stovetime.core.data.repo.progress.ProgressRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.progress.RateCookRepository
import com.ericwathome.stovetime.core.data.repo.progress.RateCookRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.progress.SaveCookRepository
import com.ericwathome.stovetime.core.data.repo.progress.SaveCookRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.ObserveCookSettingsRepository
import com.ericwathome.stovetime.core.data.repo.settings.ObserveCookSettingsRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.SaveKeepScreenOnRepository
import com.ericwathome.stovetime.core.data.repo.settings.SaveKeepScreenOnRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.SaveWeeklyGoalRepository
import com.ericwathome.stovetime.core.data.repo.settings.SaveWeeklyGoalRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepositoryImpl
import com.ericwathome.stovetime.datasources.local.di.LocalModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ProgressRepositoryModule =
    module {
        // parent
        singleOf(::ProgressRepositoryImpl) {
            bind<ProgressRepository>()
        }
        // children
        singleOf(::SaveCookRepositoryImpl) {
            bind<SaveCookRepository>()
        }
        singleOf(::RateCookRepositoryImpl) {
            bind<RateCookRepository>()
        }
        singleOf(::ObserveCooksRepositoryImpl) {
            bind<ObserveCooksRepository>()
        }
        singleOf(::ClearProgressRepositoryImpl) {
            bind<ClearProgressRepository>()
        }
    }

internal val SettingsRepositoryModule =
    module {
        // parent
        singleOf(::SettingsRepositoryImpl) {
            bind<SettingsRepository>()
        }
        // children
        singleOf(::ObserveCookSettingsRepositoryImpl) {
            bind<ObserveCookSettingsRepository>()
        }
        singleOf(::SaveWeeklyGoalRepositoryImpl) {
            bind<SaveWeeklyGoalRepository>()
        }
        singleOf(::SaveKeepScreenOnRepositoryImpl) {
            bind<SaveKeepScreenOnRepository>()
        }
    }

internal val CourseRepositoryModule =
    module {
        // parent
        singleOf(::CourseRepositoryImpl) {
            bind<CourseRepository>()
        }
        // children
        singleOf(::GetCourseRepositoryImpl) {
            bind<GetCourseRepository>()
        }
    }

internal val RepositoryModule =
    module {
        includes(CourseRepositoryModule, ProgressRepositoryModule, SettingsRepositoryModule)
    }

val CoreDataModule =
    module {
        includes(LocalModule, RepositoryModule)
    }
