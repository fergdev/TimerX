package com.timerx.di

import com.russhwolf.settings.Settings
import com.timerx.platform.platformCapabilities
import com.timerx.settings.TimerXSettings
import com.timerx.settings.TimerXSettingsImpl
import com.timerx.timermanager.TimerManager
import com.timerx.ui.di.containerModule
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    includes(platformModule)
    includes(containerModule)
    single<TimerXSettings> {
        TimerXSettingsImpl(
            settings = Settings(),
            platformCapabilities = platformCapabilities,
            dispatcher = Dispatchers.Main
        )
    }
    singleOf(::TimerManager)
}

expect val platformModule: Module
