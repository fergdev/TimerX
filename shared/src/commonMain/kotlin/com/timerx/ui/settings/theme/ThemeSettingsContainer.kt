package com.timerx.ui.settings.theme

import com.timerx.platform.platformCapabilities
import com.timerx.settings.ThemeSettingsManager
import com.timerx.ui.di.ConfigurationFactory
import com.timerx.ui.di.configure
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateContrast
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateDarkTheme
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsAmoled
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsHighFidelity
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsSystemDynamic
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdatePaletteStyle
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateSeedColor
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
