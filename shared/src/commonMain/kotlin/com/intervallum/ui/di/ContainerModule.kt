package com.intervallum.ui.di

import com.intervallum.ui.create.CreateContainer
import com.intervallum.ui.main.MainContainer
import com.intervallum.ui.run.RunContainer
import com.intervallum.ui.settings.di.settingsContainerModule
import com.intervallum.util.Json
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
