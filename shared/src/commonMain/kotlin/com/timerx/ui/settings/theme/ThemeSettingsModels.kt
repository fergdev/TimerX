package com.timerx.ui.settings.theme

import androidx.compose.ui.graphics.Color
import com.materialkolor.Contrast
import com.materialkolor.PaletteStyle
import com.timerx.settings.SettingsDarkTheme
import com.timerx.ui.common.blue
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

internal interface ThemeSettingsState : MVIState {

    data object Loading : ThemeSettingsState

    data class LoadedState(
        val isDynamicThemeSupported: Boolean = false,
        val isSystemDynamic: Boolean = false,
        val settingsDarkTheme: SettingsDarkTheme = SettingsDarkTheme.User,
        val isAmoled: Boolean = true,
        val seedColor: Color = blue,
        val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
        val isHighFidelity: Boolean = false,
        val contrast: Double = Contrast.Default.value
    ) : ThemeSettingsState
}

internal interface ThemeSettingsIntent : MVIIntent {
    data class UpdateDarkTheme(val settingsDarkTheme: SettingsDarkTheme) : ThemeSettingsIntent
    data class UpdateIsSystemDynamic(val isSystemDynamic: Boolean) : ThemeSettingsIntent
    data class UpdateIsAmoled(val isAmoled: Boolean) : ThemeSettingsIntent
    data class UpdateIsHighFidelity(val isHighFidelity: Boolean) : ThemeSettingsIntent
    data class UpdateContrast(val contrast: Double) : ThemeSettingsIntent
    data class UpdatePaletteStyle(val paletteStyle: PaletteStyle) : ThemeSettingsIntent
    data class UpdateSeedColor(val seedColor: Color): ThemeSettingsIntent
}