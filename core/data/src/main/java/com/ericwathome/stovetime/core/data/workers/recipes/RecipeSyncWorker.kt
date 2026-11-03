package com.ericwathome.stovetime.core.data.workers.recipes

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.ericwathome.stovetime.core.data.helpers.networkConstraints
import com.ericwathome.stovetime.core.data.helpers.toWorkerResult
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import java.time.Duration
import java.util.concurrent.TimeUnit

const val RECIPE_SYNC_WORK_NAME = "RecipeSyncWorker"
const val RECIPE_SYNC_NOW_WORK_NAME = "RecipeSyncNowWorker"

fun Context.schedulePeriodicRecipeSyncWork() {
    val request =
        PeriodicWorkRequestBuilder<RecipeSyncWorker>(Duration.ofDays(1))
            .setConstraints(networkConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .addTag(RECIPE_SYNC_WORK_NAME)
            .build()

    WorkManager
        .getInstance(this)
        .enqueueUniquePeriodicWork(RECIPE_SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
}

fun Context.startRecipeSyncWork() {
    val request =
        OneTimeWorkRequestBuilder<RecipeSyncWorker>()
            .setConstraints(networkConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .addTag(RECIPE_SYNC_NOW_WORK_NAME)
            .build()

    WorkManager
        .getInstance(this)
        .enqueueUniqueWork(RECIPE_SYNC_NOW_WORK_NAME, ExistingWorkPolicy.KEEP, request)
}

class RecipeSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val recipesRepository: RecipesRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = recipesRepository.syncCatalogue().toWorkerResult(runAttemptCount)
}
