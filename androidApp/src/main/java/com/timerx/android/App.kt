package com.timerx.android

import android.app.Application
import com.timerx.database.DatabaseDriverFactoryImpl
import com.timerx.database.application
import com.timerx.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class App : Application() {
    private val androidModule by lazy {
        module {
            single { DatabaseDriverFactoryImpl(get()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(androidModule + sharedModule())
        }
    }
}