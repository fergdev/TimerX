package com.timerx.ui.settings.di

import com.timerx.ui.settings.about.main.AboutMainContainer
import com.timerx.ui.settings.about.main.AboutMainIntent
import com.timerx.ui.settings.about.main.AboutMainState
import com.timerx.ui.settings.alerts.AlertsSettingsContainer
import com.timerx.ui.settings.background.BackgroundSettingsContainer
import com.timerx.ui.settings.main.MainSettingsContainer
import com.timerx.ui.settings.theme.ThemeSettingsContainer
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pro.respawn.flowmvi.api.Container

val settingsModule = module {
    factoryOf(::AlertsSettingsContainer)
    factoryOf(::ThemeSettingsContainer)
    factoryOf(::BackgroundSettingsContainer)
    factoryOf(::MainSettingsContainer)
    factoryOf(::AboutMainContainer) {
        bind<Container<AboutMainState, AboutMainIntent, Nothing>>()
    }
}
