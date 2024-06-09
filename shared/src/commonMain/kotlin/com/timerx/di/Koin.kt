package com.timerx.di

import com.timerx.beep.VolumeManager
import com.timerx.beep.getBeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepo
import com.timerx.notification.TimerXNotificationManager
import com.timerx.platform.Platform
import com.timerx.settings.SettingsManager
import com.timerx.settings.TimerXSettings
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.run.RunViewModel
import com.timerx.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    single { TimerXSettings(SettingsManager()) }
    factory { VolumeManager(get()) }
    single { getBeepMaker(get()) }
    single<ITimerRepository> { TimerRepo() }
    single { TimerXNotificationManager() }
    factory { MainViewModel(get()) }
    factory { (timerName: String) -> CreateViewModel(timerName, get()) }
    factory { (timerName: String) -> RunViewModel(timerName, get(), get(), get(), get()) }
    factory { SettingsViewModel(get()) }
}
val platformModule = module { singleOf(::Platform) }

fun appModule() = listOf(sharedModule, platformModule)