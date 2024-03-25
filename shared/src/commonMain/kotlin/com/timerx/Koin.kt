package com.timerx

import com.timerx.beep.getBeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepo
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.run.RunViewModel
import org.koin.dsl.module

fun sharedModule() = module {
    single { getBeepMaker() }
    single<ITimerRepository> { TimerRepo() }
    factory { MainViewModel(get()) }
    factory { (timerName: String) -> CreateViewModel(timerName, get()) }
    factory { (timerName: String) -> RunViewModel(timerName, get(), get()) }
}
