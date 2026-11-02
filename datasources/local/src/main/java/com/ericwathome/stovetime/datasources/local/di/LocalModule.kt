package com.ericwathome.stovetime.datasources.local.di

import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.ericwathome.stovetime.core.common.helpers.ApplicationScope
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.database.StovetimeDatabase
import com.ericwathome.stovetime.datasources.local.helpers.PreferencesFile
import com.ericwathome.stovetime.datasources.local.helpers.createPreferencesDataStore
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.DeleteAllCooksLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.DeleteAllCooksLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.ObserveCooksLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.ObserveCooksLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.RateCookLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.RateCookLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.SaveCookLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.SaveCookLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasourceImpl
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val DatabaseModule =
    module {
        single {
            Room
                .databaseBuilder(androidContext(), StovetimeDatabase::class.java, StovetimeDatabase.NAME)
                .build()
        }
    }

internal val DaoModule =
    module {
        // progress DAOs
        single<CookDao> { get<StovetimeDatabase>().cookDao }
    }

internal val CookLocalDatasourceModule =
    module {
        // parent
        singleOf(::CookLocalDatasourceImpl) {
            bind<CookLocalDatasource>()
        }
        // children
        singleOf(::SaveCookLocalDatasourceImpl) {
            bind<SaveCookLocalDatasource>()
        }
        singleOf(::RateCookLocalDatasourceImpl) {
            bind<RateCookLocalDatasource>()
        }
        singleOf(::ObserveCooksLocalDatasourceImpl) {
            bind<ObserveCooksLocalDatasource>()
        }
        singleOf(::DeleteAllCooksLocalDatasourceImpl) {
            bind<DeleteAllCooksLocalDatasource>()
        }
    }

internal val PreferencesDataStoreModule =
    module {
        PreferencesFile.entries.forEach { file ->
            single(named(file.fileName)) {
                createPreferencesDataStore(
                    scope = get<ApplicationScope>() + get<StovetimeDispatchers>().io + SupervisorJob(),
                    produceFile = { androidContext().preferencesDataStoreFile(file.fileName) },
                )
            }
        }
    }

internal val PreferencesLocalDatasourceModule =
    module {
        single<CookSettingsLocalDatasource> {
            CookSettingsLocalDatasourceImpl(
                dataStore = get(named(PreferencesFile.CookSettings.fileName)),
                dispatchers = get(),
            )
        }
    }

internal val SourcesModule =
    module {
        includes(CookLocalDatasourceModule, PreferencesLocalDatasourceModule)
    }

val LocalModule =
    module {
        includes(DatabaseModule, DaoModule, PreferencesDataStoreModule, SourcesModule)
    }
