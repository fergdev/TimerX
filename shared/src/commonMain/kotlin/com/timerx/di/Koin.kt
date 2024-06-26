package com.timerx.di

import com.timerx.analytics.getTimerXAnalytics
import com.timerx.beep.getBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.database.RealmTimerRepository
import com.timerx.database.createRoomDatabaseFactory
import com.timerx.notification.getTimerXNotificationManager
import com.timerx.settings.TimerXSettings
import com.timerx.settings.dataStorePreferences
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.run.RunViewModel
import com.timerx.ui.settings.SettingsViewModel
import com.timerx.vibration.getVibrationManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.koin.dsl.module


@OptIn(DelicateCoroutinesApi::class)
val sharedModule = module {
    single {
        dataStorePreferences(
            corruptionHandler = null,
            coroutineScope = GlobalScope
        )
    }
    single { TimerXSettings(get()) }
    single { getBeepManager(get()) }
    single { getTimerXAnalytics() }
    single { getVibrationManager(get()) }
    single<ITimerRepository> { RealmTimerRepository(createRoomDatabaseFactory().createRoomDataBase()) }
    single { getTimerXNotificationManager() }
    factory { MainViewModel(get()) }
    factory { (timerId: String) -> CreateViewModel(timerId.idToLong(), get(), get(), get()) }
    factory { (timerId: String) ->
        println("TimerId $timerId")
        RunViewModel(
            timerId.idToLong(),
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

private fun String.idToLong(): Long {
    if (this.isEmpty()) return -1L
    return this.toLong()
}