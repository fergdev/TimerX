package com.timerx.android

import android.app.Application
import com.timerx.database.application
import com.timerx.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(sharedModule())
        }
    }
}