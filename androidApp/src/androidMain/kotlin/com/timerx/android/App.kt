package com.timerx.android

import android.app.Application
import com.timerx.database.application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
    }
}