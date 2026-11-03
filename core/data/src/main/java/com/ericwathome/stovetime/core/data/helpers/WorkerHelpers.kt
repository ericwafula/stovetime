package com.ericwathome.stovetime.core.data.helpers

import androidx.work.Constraints
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import com.ericwathome.stovetime.core.common.result.DataResult

internal const val MAX_WORKER_ATTEMPTS = 3

internal fun workerResult(runAttemptCount: Int): ListenableWorker.Result =
    if (runAttemptCount + 1 >= MAX_WORKER_ATTEMPTS) ListenableWorker.Result.failure() else ListenableWorker.Result.retry()

internal fun <T> DataResult<T>.toWorkerResult(runAttemptCount: Int): ListenableWorker.Result =
    when (this) {
        is DataResult.Success -> ListenableWorker.Result.success()
        is DataResult.Error -> workerResult(runAttemptCount)
    }

internal fun networkConstraints(): Constraints =
    Constraints
        .Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
