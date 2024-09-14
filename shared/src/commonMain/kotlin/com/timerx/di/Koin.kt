package com.timerx.di

//import com.timerx.database.TimerRepository
//import com.timerx.database.createRoomDatabaseFactory
import com.timerx.analytics.getTimerXAnalytics
import com.timerx.beep.getBeepManager
import com.timerx.domain.TimerManager
import com.timerx.notification.getTimerXNotificationManager
import com.timerx.permissions.permissionsHandler
import com.timerx.ui.create.CreateContainer
import com.timerx.ui.main.MainContainer
import com.timerx.ui.navigation.NavigationProvider
import com.timerx.ui.run.RunContainer
import com.timerx.ui.settings.SettingsContainer
import com.timerx.vibration.getVibrationManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    includes(platformModule)
    single { new(::getBeepManager) }
    single { new(::getTimerXAnalytics) }
    single { new(::getVibrationManager) }
    single { new(::permissionsHandler) }
    singleOf(::TimerManager)
    single { new(::getTimerXNotificationManager) }
    singleOf(::NavigationProvider)
    factoryOf(::MainContainer)
    factoryOf(::CreateContainer)
    factoryOf(::RunContainer)
    factoryOf(::SettingsContainer)
}

expect val platformModule: Module
