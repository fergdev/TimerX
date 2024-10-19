package com.timerx.di

import com.timerx.settings.TimerXSettings
import com.timerx.settings.TimerXSettingsImpl
import com.timerx.timermanager.TimerManager
import com.timerx.ui.di.containerModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    includes(platformModule)
    includes(containerModule)
    singleOf(::TimerXSettingsImpl) { bind<TimerXSettings>() }
    singleOf(::TimerManager)
}

expect val platformModule: Module
