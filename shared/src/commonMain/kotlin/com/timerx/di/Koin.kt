package com.timerx.di

import com.timerx.beep.getBeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepo
import com.timerx.notification.TimerXNotificationManager
import com.timerx.settings.TimerXSettings
import com.timerx.settings.getSettingsManager
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.run.RunViewModel
import com.timerx.ui.settings.SettingsViewModel
import org.koin.dsl.module

val sharedModule = module {
    single { TimerXSettings(getSettingsManager()) }
    single { getBeepMaker(get()) }
    single<ITimerRepository> { TimerRepo() }
    single { TimerXNotificationManager() }
    factory { MainViewModel(get()) }
    factory { (timerName: String) -> CreateViewModel(timerName, get()) }
    factory { (timerName: String) -> RunViewModel(timerName, get(), get(), get(), get()) }
    factory { SettingsViewModel(get()) }
}

fun appModule() = listOf(sharedModule)