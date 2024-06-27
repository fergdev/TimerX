package com.timerx.android

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
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
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        FirebaseAnalytics.getInstance(this);
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(appModule() + androidModule)
        }
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@App) {}
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(
                    listOf("ABCDEF012345")
                ).build()
            )
        }
    }
}