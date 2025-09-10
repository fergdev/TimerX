package com.intervallum.ui.settings.di

import com.intervallum.ui.settings.alerts.AlertsSettingsContainer
import com.intervallum.ui.settings.background.BackgroundSettingsContainer
import com.intervallum.ui.settings.main.MainSettingsContainer
import com.intervallum.ui.settings.theme.ThemeSettingsContainer
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsContainerModule = module {
    factoryOf(::AlertsSettingsContainer)
    factoryOf(::ThemeSettingsContainer)
    factoryOf(::BackgroundSettingsContainer)
    factoryOf(::MainSettingsContainer)
}
