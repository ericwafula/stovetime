package com.ericwathome.stovetime

import android.app.Application
import com.ericwathome.stovetime.core.data.workers.recipes.ScheduleRecipeSyncWork
import com.ericwathome.stovetime.di.AppModule
import com.ericwathome.stovetime.sdk.timer.StovetimeTimers
import com.ericwathome.stovetime.sdk.timer.TimerConfig
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class StovetimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        StovetimeTimers.initialize(
            context = this,
            config =
                TimerConfig(
                    smallIcon = R.drawable.ic_stat_timer,
                    openAppIntent = { payload -> MainActivity.timerIntent(this, payload) },
                    alarmActivity = AlarmActivity::class.java,
                ),
        )
        val koin =
            startKoin {
                androidContext(this@StovetimeApplication)
                workManagerFactory()
                modules(AppModule)
            }.koin
        koin.get<ScheduleRecipeSyncWork>().invoke()
    }
}
