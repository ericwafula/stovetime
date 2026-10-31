package com.ericwathome.stovetime

import android.app.Application
import com.ericwathome.stovetime.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StovetimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StovetimeApplication)
            modules(AppModule)
        }
    }
}
