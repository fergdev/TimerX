package com.timerx.di

import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepository
import com.timerx.database.createRoomDatabaseFactory
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mobileModule = module {
    single { createRoomDatabaseFactory().createRoomDataBase() }
    singleOf(::TimerRepository) { bind<ITimerRepository>() }
}
