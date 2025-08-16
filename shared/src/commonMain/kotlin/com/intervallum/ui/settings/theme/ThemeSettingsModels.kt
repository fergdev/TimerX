package com.intervallum.ui.settings.theme

import androidx.compose.ui.graphics.Color
import com.intervallum.settings.SettingsDarkTheme
import com.intervallum.settings.ThemeContrast
import com.intervallum.ui.common.blue
import com.materialkolor.PaletteStyle
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface ThemeSettingsState : MVIState {

    data object Loading : ThemeSettingsState

    data class LoadedState(
        val isDynamicThemeSupported: Boolean = false,
        val isSystemDynamic: Boolean = false,
        val settingsDarkTheme: SettingsDarkTheme = SettingsDarkTheme.User,
        val isAmoled: Boolean = true,
        val seedColor: Color = blue,
        val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
        val isHighFidelity: Boolean = false,
        val contrast: ThemeContrast = ThemeContrast.default
    ) : ThemeSettingsState
}

interface ThemeSettingsIntent : MVIIntent {
    data class UpdateDarkTheme(val settingsDarkTheme: SettingsDarkTheme) : ThemeSettingsIntent
    data class UpdateIsSystemDynamic(val isSystemDynamic: Boolean) : ThemeSettingsIntent
    data class UpdateIsAmoled(val isAmoled: Boolean) : ThemeSettingsIntent
    data class UpdateIsHighFidelity(val isHighFidelity: Boolean) : ThemeSettingsIntent
    data class UpdateContrast(val contrast: ThemeContrast) : ThemeSettingsIntent
    data class UpdatePaletteStyle(val paletteStyle: PaletteStyle) : ThemeSettingsIntent
    data class UpdateSeedColor(val seedColor: Color) : ThemeSettingsIntent
}
