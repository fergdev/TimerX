package com.timerx

import org.koin.core.context.startKoin

fun initKoin() {
    println("Koin helper iosMain")
    startKoin {
        modules(
            sharedModule()
        )
    }
}