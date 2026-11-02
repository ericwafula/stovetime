package com.ericwathome.stovetime.core.data.workers.recipes

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

fun interface ScheduleRecipeSyncWork {
    operator fun invoke()
}

fun interface StartRecipeSyncWork {
    operator fun invoke()
}

fun interface ObserveRecipeSyncRunning {
    operator fun invoke(): Flow<Boolean>
}

internal class ScheduleRecipeSyncWorkImpl(
    private val context: Context,
) : ScheduleRecipeSyncWork {
    override fun invoke() = context.schedulePeriodicRecipeSyncWork()
}

internal class StartRecipeSyncWorkImpl(
    private val context: Context,
) : StartRecipeSyncWork {
    override fun invoke() = context.startRecipeSyncWork()
}

internal class ObserveRecipeSyncRunningImpl(
    private val context: Context,
) : ObserveRecipeSyncRunning {
    override fun invoke(): Flow<Boolean> {
        val workManager = WorkManager.getInstance(context)
        return combine(
            workManager.getWorkInfosForUniqueWorkFlow(RECIPE_SYNC_WORK_NAME),
            workManager.getWorkInfosForUniqueWorkFlow(RECIPE_SYNC_NOW_WORK_NAME),
        ) { scheduled, requested ->
            isRecipeSyncRunning(scheduled = scheduled.map { it.state }, requested = requested.map { it.state })
        }.distinctUntilChanged()
    }
}

internal fun isRecipeSyncRunning(
    scheduled: List<WorkInfo.State>,
    requested: List<WorkInfo.State>,
): Boolean =
    WorkInfo.State.RUNNING in scheduled ||
        requested.any { it == WorkInfo.State.RUNNING || it == WorkInfo.State.ENQUEUED }
