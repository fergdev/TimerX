package com.timerx.ui.di

import com.timerx.ui.create.CreateContainer
import com.timerx.ui.main.MainContainer
import com.timerx.ui.run.RunContainer
import com.timerx.ui.settings.di.settingsContainerModule
import com.timerx.util.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val containerModule = module {
    single { Json }
    singleOf(::DefaultConfigurationFactory) {
        bind<ConfigurationFactory>()
    }
    factoryOf(::MainContainer)
    factoryOf(::CreateContainer)
    factoryOf(::RunContainer)
    includes(settingsContainerModule)
}
