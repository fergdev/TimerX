package com.timerx.di

import com.timerx.domain.TimerManager
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.TimerXSettings
import com.timerx.ui.di.containerModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    includes(platformModule)
    includes(containerModule)
    singleOf(::TimerXSettings) { bind<ITimerXSettings>() }
    singleOf(::TimerManager)
}

expect val platformModule: Module
