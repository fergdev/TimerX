package com.timerx.ui.settings.di

import com.timerx.ui.settings.alerts.AlertsSettingsContainer
import com.timerx.ui.settings.background.BackgroundSettingsContainer
import com.timerx.ui.settings.main.MainSettingsContainer
import com.timerx.ui.settings.theme.ThemeSettingsContainer
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsContainerModule = module {
    factoryOf(::AlertsSettingsContainer)
    factoryOf(::ThemeSettingsContainer)
    factoryOf(::BackgroundSettingsContainer)
    factoryOf(::MainSettingsContainer)
}
