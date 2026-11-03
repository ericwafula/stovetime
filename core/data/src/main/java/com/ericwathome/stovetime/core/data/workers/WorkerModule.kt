package com.ericwathome.stovetime.core.data.workers

import com.ericwathome.stovetime.core.data.workers.recipes.ObserveRecipeSyncRunning
import com.ericwathome.stovetime.core.data.workers.recipes.ObserveRecipeSyncRunningImpl
import com.ericwathome.stovetime.core.data.workers.recipes.RecipeSyncWorker
import com.ericwathome.stovetime.core.data.workers.recipes.ScheduleRecipeSyncWork
import com.ericwathome.stovetime.core.data.workers.recipes.ScheduleRecipeSyncWorkImpl
import com.ericwathome.stovetime.core.data.workers.recipes.StartRecipeSyncWork
import com.ericwathome.stovetime.core.data.workers.recipes.StartRecipeSyncWorkImpl
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val WorkerModule =
    module {
        workerOf(::RecipeSyncWorker)
        singleOf(::ScheduleRecipeSyncWorkImpl) {
            bind<ScheduleRecipeSyncWork>()
        }
        singleOf(::StartRecipeSyncWorkImpl) {
            bind<StartRecipeSyncWork>()
        }
        singleOf(::ObserveRecipeSyncRunningImpl) {
            bind<ObserveRecipeSyncRunning>()
        }
    }
