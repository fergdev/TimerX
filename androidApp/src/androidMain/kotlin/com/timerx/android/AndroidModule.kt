package com.timerx.android

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val androidModule = module {
    singleOf(::GreetPresenter)
//    single<NotificationManager> { NotificationManager() }
}