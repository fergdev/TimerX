package com.timerx.android.di

import android.app.Application
import com.timerx.di.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

fun Application.startKoin() = startKoin builder@{
    androidContext(this@startKoin)
    androidLogger()
    modules(androidModule)
}
