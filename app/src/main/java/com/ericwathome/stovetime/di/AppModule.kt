package com.ericwathome.stovetime.di

import com.ericwathome.stovetime.core.common.di.CommonModule
import com.ericwathome.stovetime.core.data.di.CoreDataModule
import com.ericwathome.stovetime.features.lessons.di.PathModule
import org.koin.dsl.module

val AppModule =
    module {
        includes(CommonModule, CoreDataModule, PathModule)
    }
