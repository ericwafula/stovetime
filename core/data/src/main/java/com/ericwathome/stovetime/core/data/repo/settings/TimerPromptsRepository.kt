package com.ericwathome.stovetime.core.data.repo.settings

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.common.result.getOrNull
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.settings.TimerPromptsLocalDatasource

interface TimerPromptsRepository {
    suspend fun wasNotificationsExplained(): Boolean

    suspend fun markNotificationsExplained(): DataResult<Boolean>

    suspend fun wasLateAlarmsExplained(): Boolean

    suspend fun markLateAlarmsExplained(): DataResult<Boolean>

    suspend fun wasLockScreenAlarmsExplained(): Boolean

    suspend fun markLockScreenAlarmsExplained(): DataResult<Boolean>
}

internal class TimerPromptsRepositoryImpl(
    private val timerPromptsLocalDatasource: TimerPromptsLocalDatasource,
) : TimerPromptsRepository {
    override suspend fun wasNotificationsExplained(): Boolean =
        timerPromptsLocalDatasource.notificationsExplained.get().mapToData { it == true }.getOrNull() == true

    override suspend fun markNotificationsExplained(): DataResult<Boolean> =
        timerPromptsLocalDatasource.notificationsExplained.save(true).mapToData { it }

    override suspend fun wasLateAlarmsExplained(): Boolean =
        timerPromptsLocalDatasource.lateAlarmsExplained.get().mapToData { it == true }.getOrNull() == true

    override suspend fun markLateAlarmsExplained(): DataResult<Boolean> =
        timerPromptsLocalDatasource.lateAlarmsExplained.save(true).mapToData { it }

    override suspend fun wasLockScreenAlarmsExplained(): Boolean =
        timerPromptsLocalDatasource.lockScreenAlarmsExplained.get().mapToData { it == true }.getOrNull() == true

    override suspend fun markLockScreenAlarmsExplained(): DataResult<Boolean> =
        timerPromptsLocalDatasource.lockScreenAlarmsExplained.save(true).mapToData { it }
}
