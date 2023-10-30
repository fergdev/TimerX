package com.timerx

import com.timerx.database.TimerDatabase
import org.koin.dsl.module

fun sharedModule() = module {
    single { TimerDatabase(get()) }
}