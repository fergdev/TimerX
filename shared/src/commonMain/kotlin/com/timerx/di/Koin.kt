package com.timerx.di

import com.timerx.domain.TimerManager
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.TimerXSettings
import com.timerx.ui.create.CreateContainer
import com.timerx.ui.di.containerModule
import com.timerx.ui.main.MainContainer
import com.timerx.ui.run.RunContainer
import com.timerx.ui.settings.SettingsContainer
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    includes(platformModule)
    includes(containerModule)
    singleOf(::TimerXSettings) { bind<ITimerXSettings>() }
    singleOf(::TimerManager)
    factoryOf(::MainContainer)
    factoryOf(::CreateContainer)
    factoryOf(::RunContainer)
    factoryOf(::SettingsContainer)
}

expect val platformModule: Module
