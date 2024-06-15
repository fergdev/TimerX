package com.timerx.di

import com.timerx.analytics.TimerXAnalytics
import com.timerx.beep.getBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.database.RealmTimerRepository
import com.timerx.notification.TimerXNotificationManager
import com.timerx.settings.TimerXSettings
import com.timerx.settings.getSettingsManager
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.run.RunViewModel
import com.timerx.ui.settings.SettingsViewModel
import com.timerx.vibration.getVibrationManager
import org.koin.dsl.module

val sharedModule = module {
    single { TimerXSettings(getSettingsManager()) }
    single { getBeepManager(get()) }
    single { TimerXAnalytics() }
    single { getVibrationManager(get()) }
    single<ITimerRepository> { RealmTimerRepository() }
    single { TimerXNotificationManager() }
    factory { MainViewModel(get()) }
    factory { (timerName: String) -> CreateViewModel(timerName, get(), get()) }
    factory { (timerName: String) ->
        RunViewModel(
            timerName,
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { SettingsViewModel(get()) }
}

fun appModule() = listOf(sharedModule)