package com.ericwathome.stovetime.core.data.repo.settings

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource

fun interface SaveKeepScreenOnRepository {
    suspend operator fun invoke(keepScreenOn: Boolean): DataResult<Boolean>
}

internal class SaveKeepScreenOnRepositoryImpl(
    private val cookSettingsLocalDatasource: CookSettingsLocalDatasource,
) : SaveKeepScreenOnRepository {
    override suspend fun invoke(keepScreenOn: Boolean): DataResult<Boolean> =
        cookSettingsLocalDatasource.keepScreenOn.save(keepScreenOn).mapToData { it }
}
