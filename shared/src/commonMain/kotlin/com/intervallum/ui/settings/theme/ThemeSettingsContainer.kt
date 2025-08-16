package com.intervallum.ui.settings.theme

import com.intervallum.platform.platformCapabilities
import com.intervallum.settings.ThemeSettingsManager
import com.intervallum.ui.di.ConfigurationFactory
import com.intervallum.ui.di.configure
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdateContrast
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdateDarkTheme
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdateIsAmoled
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdateIsHighFidelity
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdateIsSystemDynamic
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdatePaletteStyle
import com.intervallum.ui.settings.theme.ThemeSettingsIntent.UpdateSeedColor
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class ThemeSettingsContainer(
    configurationFactory: ConfigurationFactory,
    private val themeSettingsManager: ThemeSettingsManager,
) : Container<ThemeSettingsState, ThemeSettingsIntent, Nothing> {

    override val store = store(ThemeSettingsState.Loading) {
        configure(configurationFactory, "Settings:Theme")
        whileSubscribed {
            themeSettingsManager.themeSettings.collect {
                updateState {
                    ThemeSettingsState.LoadedState(
                        seedColor = it.seedColor,
                        isDynamicThemeSupported = platformCapabilities.canSystemDynamicTheme,
                        isSystemDynamic = it.isSystemDynamic,
                        settingsDarkTheme = it.settingsDarkTheme,
                        isAmoled = it.isAmoled,
                        isHighFidelity = it.isHighFidelity,
                        paletteStyle = it.paletteStyle,
                        contrast = it.contrast
                    )
                }
            }
        }

        reduce {
            when (it) {
                is UpdateDarkTheme -> themeSettingsManager.setDarkTheme(it.settingsDarkTheme)
                is UpdateIsSystemDynamic -> themeSettingsManager.setIsDynamicTheme(it.isSystemDynamic)
                is UpdateIsAmoled -> themeSettingsManager.setIsAmoled(it.isAmoled)
                is UpdateIsHighFidelity -> themeSettingsManager.setIsHighFidelity(it.isHighFidelity)
                is UpdateContrast -> themeSettingsManager.setContrast(it.contrast)
                is UpdatePaletteStyle -> themeSettingsManager.setPaletteStyle(it.paletteStyle)
                is UpdateSeedColor -> themeSettingsManager.setSeedColor(it.seedColor)
            }
        }
    }
}
