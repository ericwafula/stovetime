package com.ericwathome.stovetime.core.data.di

import com.ericwathome.stovetime.core.data.repo.cook.CookRepository
import com.ericwathome.stovetime.core.data.repo.cook.CookRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.cook.GetCookPlanRepository
import com.ericwathome.stovetime.core.data.repo.cook.GetCookPlanRepositoryImpl
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
import com.ericwathome.stovetime.core.data.repo.recipes.GetRecipeRepository
import com.ericwathome.stovetime.core.data.repo.recipes.GetRecipeRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.recipes.ObserveCatalogueRepository
import com.ericwathome.stovetime.core.data.repo.recipes.ObserveCatalogueRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.recipes.ObserveRecipeSyncStatusRepository
import com.ericwathome.stovetime.core.data.repo.recipes.ObserveRecipeSyncStatusRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.recipes.SyncCatalogueRepository
import com.ericwathome.stovetime.core.data.repo.recipes.SyncCatalogueRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.ObserveCookSettingsRepository
import com.ericwathome.stovetime.core.data.repo.settings.ObserveCookSettingsRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.SaveKeepScreenOnRepository
import com.ericwathome.stovetime.core.data.repo.settings.SaveKeepScreenOnRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.SaveWeeklyGoalRepository
import com.ericwathome.stovetime.core.data.repo.settings.SaveWeeklyGoalRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.settings.TimerPromptsRepository
import com.ericwathome.stovetime.core.data.repo.settings.TimerPromptsRepositoryImpl
import com.ericwathome.stovetime.core.data.repo.timers.TimersRepository
import com.ericwathome.stovetime.core.data.repo.timers.TimersRepositoryImpl
import com.ericwathome.stovetime.sdk.timer.StovetimeTimers
import com.ericwathome.stovetime.core.data.workers.WorkerModule
import com.ericwathome.stovetime.datasources.local.di.LocalModule
import com.ericwathome.stovetime.datasources.network.di.NetworkModule
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

internal val CookRepositoryModule =
    module {
        // parent
        singleOf(::CookRepositoryImpl) {
            bind<CookRepository>()
        }
        // children
        singleOf(::GetCookPlanRepositoryImpl) {
            bind<GetCookPlanRepository>()
        }
    }

internal val TimersRepositoryModule =
    module {
        single { StovetimeTimers.engine }
        singleOf(::TimersRepositoryImpl) {
            bind<TimersRepository>()
        }
        singleOf(::TimerPromptsRepositoryImpl) {
            bind<TimerPromptsRepository>()
        }
    }

internal val RecipesRepositoryModule =
    module {
        // parent
        singleOf(::RecipesRepositoryImpl) {
            bind<RecipesRepository>()
        }
        // children
        singleOf(::SyncCatalogueRepositoryImpl) {
            bind<SyncCatalogueRepository>()
        }
        singleOf(::ObserveCatalogueRepositoryImpl) {
            bind<ObserveCatalogueRepository>()
        }
        singleOf(::ObserveRecipeSyncStatusRepositoryImpl) {
            bind<ObserveRecipeSyncStatusRepository>()
        }
        singleOf(::GetRecipeRepositoryImpl) {
            bind<GetRecipeRepository>()
        }
    }

internal val RepositoryModule =
    module {
        includes(CookRepositoryModule, CourseRepositoryModule, ProgressRepositoryModule, SettingsRepositoryModule, TimersRepositoryModule, RecipesRepositoryModule)
    }

val CoreDataModule =
    module {
        includes(LocalModule, NetworkModule, RepositoryModule, WorkerModule)
    }
