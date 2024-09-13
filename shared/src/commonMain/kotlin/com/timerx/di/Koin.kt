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
import com.timerx.ui.create.CreateContainer
import com.timerx.ui.main.MainContainer
import com.timerx.ui.navigation.NavigationProvider
import com.timerx.ui.run.RunContainer
import com.timerx.ui.settings.SettingsContainer
import com.timerx.vibration.getVibrationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    single { dataStorePreferences(coroutineScope = CoroutineScope(Dispatchers.IO)) }
    singleOf(::TimerXSettings)
    single { new(::getBeepManager) }
    single { new(::getTimerXAnalytics) }
    single { new(::getVibrationManager) }
    single { new(::permissionsHandler) }
    singleOf(::TimerManager)
    single { createRoomDatabaseFactory().createRoomDataBase() }
    singleOf(::TimerRepository) { bind<ITimerRepository>() }
    single { new(::getTimerXNotificationManager) }
    singleOf(::NavigationProvider)
    factoryOf(::MainContainer)
    factoryOf(::CreateContainer)
    factoryOf(::RunContainer)
    factoryOf(::SettingsContainer)
}
