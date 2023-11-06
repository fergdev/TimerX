package com.timerx

import com.timerx.beep.getBeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.database.TimerDatabase
import com.timerx.database.getDatabaseDriverFactory
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.run.RunViewModel
import org.koin.dsl.module

fun sharedModule() = module {
    single { getDatabaseDriverFactory() }
    single { getBeepMaker() }
    single<ITimerRepository> { TimerDatabase(get()) }
    factory { MainViewModel(get()) }
    factory { (timerId: Long) -> CreateViewModel(timerId, get()) }
    factory { (timerId: Long) -> RunViewModel(timerId, get(), get()) }
}