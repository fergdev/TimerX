package com.timerx.ui.di

import com.timerx.ui.create.CreateContainer
import com.timerx.ui.main.MainContainer
import com.timerx.ui.run.RunContainer
import com.timerx.ui.settings.di.settingsContainerModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val containerModule = module {
    factoryOf(::MainContainer)
    factoryOf(::CreateContainer)
    factoryOf(::RunContainer)
    includes(settingsContainerModule)
}
