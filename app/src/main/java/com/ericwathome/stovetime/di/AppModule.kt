package com.ericwathome.stovetime.di

import com.ericwathome.stovetime.core.common.di.CommonModule
import com.ericwathome.stovetime.core.data.di.CoreDataModule
import com.ericwathome.stovetime.features.lessons.di.LessonsModule
import com.ericwathome.stovetime.features.me.di.MeModule
import com.ericwathome.stovetime.features.recipes.di.RecipesModule
import com.ericwathome.stovetime.features.timers.di.TimersModule
import com.ericwathome.stovetime.widget.TimersWidgetUpdater
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val WidgetModule =
    module {
        single { TimersWidgetUpdater(context = androidContext(), applicationScope = get()) }
    }

val AppModule =
    module {
        includes(CommonModule, CoreDataModule, LessonsModule, RecipesModule, TimersModule, MeModule, WidgetModule, ReminderModule)
    }
