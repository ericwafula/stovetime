package com.ericwathome.stovetime.datasources.local.di

import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.ericwathome.stovetime.core.common.helpers.ApplicationScope
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.datasources.local.daos.CatalogueDao
import com.ericwathome.stovetime.datasources.local.daos.CookDao
import com.ericwathome.stovetime.datasources.local.daos.RecipeDao
import com.ericwathome.stovetime.datasources.local.database.StovetimeDatabase
import com.ericwathome.stovetime.datasources.local.helpers.AssetContentReader
import com.ericwathome.stovetime.datasources.local.helpers.BundledContentReader
import com.ericwathome.stovetime.datasources.local.helpers.PreferencesFile
import com.ericwathome.stovetime.datasources.local.helpers.createPreferencesDataStore
import com.ericwathome.stovetime.datasources.local.sources.course.CourseLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.course.CourseLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.course.GetCourseLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.course.GetCourseLocalDatasourceImpl
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
import com.ericwathome.stovetime.datasources.local.sources.settings.TimerPromptsLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.settings.TimerPromptsLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.GetCookedRecipeIdsLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.GetCookedRecipeIdsLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.CatalogueLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.CatalogueLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.ObserveCategoriesLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.ObserveCategoriesLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.ObserveCatalogueMealsLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.ObserveCatalogueMealsLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.GetCatalogueMealsLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.GetCatalogueMealsLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.ReplaceCatalogueLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.ReplaceCatalogueLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.RecipeLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.RecipeLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.GetRecipeLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.GetRecipeLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.SaveRecipeLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.recipe.SaveRecipeLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.settings.RecipeSyncLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.settings.RecipeSyncLocalDatasourceImpl
import com.ericwathome.stovetime.datasources.local.sources.settings.WeeklyReminderLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.settings.WeeklyReminderLocalDatasourceImpl
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
        // recipes DAOs
        single<CatalogueDao> { get<StovetimeDatabase>().catalogueDao }
        single<RecipeDao> { get<StovetimeDatabase>().recipeDao }
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
        singleOf(::GetCookedRecipeIdsLocalDatasourceImpl) {
            bind<GetCookedRecipeIdsLocalDatasource>()
        }
    }

internal val CatalogueLocalDatasourceModule =
    module {
        // parent
        singleOf(::CatalogueLocalDatasourceImpl) {
            bind<CatalogueLocalDatasource>()
        }
        // children
        singleOf(::ObserveCategoriesLocalDatasourceImpl) {
            bind<ObserveCategoriesLocalDatasource>()
        }
        singleOf(::ObserveCatalogueMealsLocalDatasourceImpl) {
            bind<ObserveCatalogueMealsLocalDatasource>()
        }
        singleOf(::GetCatalogueMealsLocalDatasourceImpl) {
            bind<GetCatalogueMealsLocalDatasource>()
        }
        singleOf(::ReplaceCatalogueLocalDatasourceImpl) {
            bind<ReplaceCatalogueLocalDatasource>()
        }
    }

internal val RecipeLocalDatasourceModule =
    module {
        // parent
        singleOf(::RecipeLocalDatasourceImpl) {
            bind<RecipeLocalDatasource>()
        }
        // children
        singleOf(::GetRecipeLocalDatasourceImpl) {
            bind<GetRecipeLocalDatasource>()
        }
        singleOf(::SaveRecipeLocalDatasourceImpl) {
            bind<SaveRecipeLocalDatasource>()
        }
    }

internal val CourseLocalDatasourceModule =
    module {
        single<BundledContentReader> { AssetContentReader(androidContext()) }
        // parent
        singleOf(::CourseLocalDatasourceImpl) {
            bind<CourseLocalDatasource>()
        }
        // children
        singleOf(::GetCourseLocalDatasourceImpl) {
            bind<GetCourseLocalDatasource>()
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
        single<TimerPromptsLocalDatasource> {
            TimerPromptsLocalDatasourceImpl(
                dataStore = get(named(PreferencesFile.TimerPrompts.fileName)),
                dispatchers = get(),
            )
        }
        single<RecipeSyncLocalDatasource> {
            RecipeSyncLocalDatasourceImpl(
                dataStore = get(named(PreferencesFile.RecipeSync.fileName)),
                dispatchers = get(),
            )
        }
        single<WeeklyReminderLocalDatasource> {
            WeeklyReminderLocalDatasourceImpl(
                dataStore = get(named(PreferencesFile.WeeklyReminder.fileName)),
                dispatchers = get(),
            )
        }
    }

internal val SourcesModule =
    module {
        includes(
            CookLocalDatasourceModule,
            CourseLocalDatasourceModule,
            CatalogueLocalDatasourceModule,
            RecipeLocalDatasourceModule,
            PreferencesLocalDatasourceModule,
        )
    }

val LocalModule =
    module {
        includes(DatabaseModule, DaoModule, PreferencesDataStoreModule, SourcesModule)
    }
