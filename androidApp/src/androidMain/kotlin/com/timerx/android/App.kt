package com.timerx.android

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.timerx.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        FirebaseAnalytics.getInstance(this);
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(appModule() + androidModule)
        }
    }
}