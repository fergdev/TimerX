package com.intervallum.di

import com.intervallum.coroutines.TxDispatchers
import com.intervallum.platform.platformCapabilities
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.settings.AlertSettingsManagerImpl
import com.intervallum.settings.BackgroundSettingsManager
import com.intervallum.settings.BackgroundSettingsManagerImpl
import com.intervallum.settings.IntervallumSettings
import com.intervallum.settings.IntervallumSettingsImpl
import com.intervallum.settings.ThemeSettingsManager
import com.intervallum.settings.ThemeSettingsManagerImpl
import com.intervallum.timermanager.TimerManager
import com.intervallum.timermanager.TimerManagerImpl
import com.intervallum.ui.di.containerModule
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
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
    single {
        TxDispatchers(
            default = Dispatchers.Default,
            main = Dispatchers.Main,
            io = Dispatchers.Unconfined
        )
    }
    single<FlowSettings> {
        Settings()
            .makeObservable()
            .toFlowSettings(dispatcher = Dispatchers.Main)
    }
    singleOf(::AlertSettingsManagerImpl) { bind<AlertSettingsManager>() }
    singleOf(::IntervallumSettingsImpl) { bind<IntervallumSettings>() }
    singleOf(::ThemeSettingsManagerImpl) { bind<ThemeSettingsManager>() }
    singleOf(::BackgroundSettingsManagerImpl) { bind<BackgroundSettingsManager>() }
    singleOf(::TimerManagerImpl) { bind<TimerManager>() }
}

expect val platformModule: Module
