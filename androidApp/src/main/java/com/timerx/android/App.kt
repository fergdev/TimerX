package com.timerx.android

import android.app.Application
import com.timerx.android.main.MainViewModel
import com.timerx.android.run.RunViewModel
import com.timerx.shareModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class App : Application() {
    private val androidModule by lazy {
        module {
            viewModel { MainViewModel(get()) }
            viewModel { RunViewModel(get(), get()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        println("App OnCreate")
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(androidModule + shareModule())
        }
    }
}