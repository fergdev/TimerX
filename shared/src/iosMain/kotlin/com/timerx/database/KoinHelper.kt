package com.timerx.database

import com.timerx.sharedModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(sharedModule())
    }
}