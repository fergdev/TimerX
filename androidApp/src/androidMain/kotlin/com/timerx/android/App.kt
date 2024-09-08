package com.timerx.android

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.timerx.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(appModule() + androidModule)
            createEagerInstances()
        }
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseAnalytics.getInstance(this@App).setAnalyticsCollectionEnabled(true)
            MobileAds.initialize(this@App) {}
        }
    }
}