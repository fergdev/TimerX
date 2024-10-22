package com.timerx.ui.settings.theme

import com.timerx.platform.platformCapabilities
import com.timerx.settings.TimerXSettings
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
    private val timerXSettings: TimerXSettings,
) : Container<ThemeSettingsState, ThemeSettingsIntent, Nothing> {

    override val store = store(ThemeSettingsState.Loading) {
        whileSubscribed {
            timerXSettings.themeSettingsManager.themeSettings.collect {
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
                is UpdateDarkTheme -> timerXSettings.themeSettingsManager.setDarkTheme(it.settingsDarkTheme)
                is UpdateIsSystemDynamic -> timerXSettings.themeSettingsManager.setIsDynamicTheme(it.isSystemDynamic)
                is UpdateIsAmoled -> timerXSettings.themeSettingsManager.setIsAmoled(it.isAmoled)
                is UpdateIsHighFidelity -> timerXSettings.themeSettingsManager.setIsHighFidelity(it.isHighFidelity)
                is UpdateContrast -> timerXSettings.themeSettingsManager.setContrast(it.contrast)
                is UpdatePaletteStyle -> timerXSettings.themeSettingsManager.setPaletteStyle(it.paletteStyle)
                is UpdateSeedColor -> timerXSettings.themeSettingsManager.setSeedColor(it.seedColor)
            }
        }
    }
}
