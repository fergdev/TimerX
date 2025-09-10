package com.intervallum.android

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.intervallum.android.di.startKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin()
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseAnalytics.getInstance(this@App).setAnalyticsCollectionEnabled(true)
            MobileAds.initialize(this@App) {}
        }
    }
}
