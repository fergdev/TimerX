package com.timerx.di

import com.timerx.analytics.getTimerXAnalytics
import com.timerx.beep.getBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepository
import com.timerx.database.createRoomDatabaseFactory
import com.timerx.domain.TimerManager
import com.timerx.notification.getTimerXNotificationManager
import com.timerx.permissions.permissionsHandler
import com.timerx.settings.TimerXSettings
import com.timerx.settings.dataStorePreferences
import com.timerx.ui.create.CreateViewModel
import com.timerx.ui.main.MainViewModel
import com.timerx.ui.navigation.NavigationProvider
import com.timerx.ui.run.RunViewModel
import com.timerx.ui.settings.SettingsContainer
import com.timerx.vibration.getVibrationManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.koin.core.module.dsl.new
import org.koin.dsl.bind
import org.koin.dsl.module


@OptIn(DelicateCoroutinesApi::class)
val sharedModule = module {
    single {
        dataStorePreferences(
            corruptionHandler = null,
            coroutineScope = GlobalScope
        )
    }
    single { new(::TimerXSettings) }
    single { new(::getBeepManager) }
    single { new(::getTimerXAnalytics) }
    single { new(::getVibrationManager) }
    single { new(::permissionsHandler) }
    single { new(::TimerManager) }
    single { createRoomDatabaseFactory().createRoomDataBase() }
    single { new(::TimerRepository) } bind  ITimerRepository::class
    single { new(::getTimerXNotificationManager) }
    single { new(::NavigationProvider) }
    factory { new(::MainViewModel) }
    factory { (timerId: String) -> CreateViewModel(timerId.idToLong(), get(), get(), get()) }
    factory { (timerId: String) ->
        RunViewModel(
            timerId.idToLong(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { new(::SettingsContainer) }
}

fun appModule() = listOf(sharedModule)

private fun String.idToLong(): Long {
    if (this.isEmpty()) return -1L
    return this.toLong()
}