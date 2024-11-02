package com.timerx.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.platform.platformCapabilities
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.AlertSettingsManagerImpl
import com.timerx.settings.BackgroundSettingsManager
import com.timerx.settings.BackgroundSettingsManagerImpl
import com.timerx.settings.ThemeSettingsManager
import com.timerx.settings.ThemeSettingsManagerImpl
import com.timerx.settings.TimerXSettings
import com.timerx.settings.TimerXSettingsImpl
import com.timerx.timermanager.TimerManager
import com.timerx.timermanager.TimerManagerImpl
import com.timerx.ui.di.containerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val sharedModule = module {
    includes(platformModule)
    includes(containerModule)
    single { platformCapabilities }
    single<FlowSettings> {
        Settings().makeObservable().toFlowSettings(dispatcher = Dispatchers.Main)
    }
    singleOf(::AlertSettingsManagerImpl) { bind<AlertSettingsManager>() }
    singleOf(::TimerXSettingsImpl) { bind<TimerXSettings>() }
    singleOf(::ThemeSettingsManagerImpl) { bind<ThemeSettingsManager>() }
    singleOf(::BackgroundSettingsManagerImpl) { bind<BackgroundSettingsManager>() }
    single<TimerManager> {
        TimerManagerImpl(
            get(), CoroutineScope(Dispatchers.Main)
        )
    }
}

expect val platformModule: Module
