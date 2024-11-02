package com.timerx.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.hoc081098.flowext.combine
import com.materialkolor.PaletteStyle
import com.materialkolor.PaletteStyle.TonalSpot
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.timerx.platform.PlatformCapabilities
import com.timerx.settings.SettingsDarkTheme.User
import com.timerx.settings.ThemeContrast.Companion.default
import com.timerx.ui.common.blue
import com.timerx.util.mapIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val THEME_SETTINGS = "themeSettings_"
private const val SEED_COLOR = "${THEME_SETTINGS}seedColor"
private const val PALETTE_STYLE = "${THEME_SETTINGS}paletteStyle"
private const val DARK_THEME = "${THEME_SETTINGS}darkTheme"
private const val DYNAMIC_THEME = "${THEME_SETTINGS}dynamicTheme"
private const val IS_AMOLED = "${THEME_SETTINGS}isAmoled"
private const val IS_HIGH_FIDELITY = "${THEME_SETTINGS}isHighFidelity"
private const val THEME_CONTRAST = "${THEME_SETTINGS}contrast"

interface ThemeSettingsManager {
    val themeSettings: Flow<ThemeSettings>
    suspend fun setSeedColor(seedColor: Color)
    suspend fun setPaletteStyle(style: PaletteStyle)
    suspend fun setIsAmoled(isAmoled: Boolean)
    suspend fun setDarkTheme(settingsDarkTheme: SettingsDarkTheme)
    suspend fun setIsDynamicTheme(isDynamic: Boolean)
    suspend fun setIsHighFidelity(isHighFidelity: Boolean)
    suspend fun setContrast(themeContrast: ThemeContrast)
}

@OptIn(ExperimentalSettingsApi::class)
internal class ThemeSettingsManagerImpl(
    private val flowSettings: FlowSettings,
    private val platformCapabilities: PlatformCapabilities
) : ThemeSettingsManager {

    private val isDynamicTheme = flowSettings.getBooleanOrNullFlow(DYNAMIC_THEME).mapIfNull(false)
    private val isSettingsDarkTheme = flowSettings.getIntOrNullFlow(DARK_THEME).map {
        if (it == null) User
        else SettingsDarkTheme.entries[it]
    }
    private val isAmoled = flowSettings.getBooleanOrNullFlow(IS_AMOLED).mapIfNull(false)
    private val seedColor =
        flowSettings.getIntOrNullFlow(SEED_COLOR).map {
            if (it == null) blue
            else Color(it)
        }
    private val style = flowSettings.getIntOrNullFlow(PALETTE_STYLE).map {
        if (it == null) TonalSpot
        else PaletteStyle.entries[it]
    }
    private val isHighFidelity =
        flowSettings.getBooleanOrNullFlow(IS_HIGH_FIDELITY).mapIfNull(false)

    private val contrast = flowSettings.getDoubleOrNullFlow(THEME_CONTRAST).map {
        if (it == null) default
        else ThemeContrast(it)
    }

    override val themeSettings = combine(
        isDynamicTheme, isSettingsDarkTheme, isAmoled, seedColor, style, isHighFidelity, contrast
    ) { isDynamicTheme, isDarkTheme, isAmoled, seedColor, style, isHighFidelity, contrast ->
        ThemeSettings(
            isSystemDynamic = isDynamicTheme,
            settingsDarkTheme = isDarkTheme,
            isAmoled = isAmoled,
            seedColor = seedColor,
            paletteStyle = style,
            isHighFidelity = isHighFidelity,
            contrast = contrast
        )
    }

    override suspend fun setSeedColor(seedColor: Color) {
        flowSettings.putInt(SEED_COLOR, seedColor.toArgb())
    }

    override suspend fun setPaletteStyle(style: PaletteStyle) {
        flowSettings.putInt(PALETTE_STYLE, style.ordinal)
    }

    override suspend fun setIsAmoled(isAmoled: Boolean) {
        flowSettings.putBoolean(IS_AMOLED, isAmoled)
    }

    override suspend fun setDarkTheme(settingsDarkTheme: SettingsDarkTheme) {
        flowSettings.putInt(DARK_THEME, settingsDarkTheme.ordinal)
    }

    override suspend fun setIsDynamicTheme(isDynamic: Boolean) {
        require(platformCapabilities.canSystemDynamicTheme) {
            "Attempting to set dynamic theme when platform does not support it"
        }
        flowSettings.putBoolean(DYNAMIC_THEME, isDynamic)
    }

    override suspend fun setIsHighFidelity(isHighFidelity: Boolean) {
        flowSettings.putBoolean(IS_HIGH_FIDELITY, isHighFidelity)
    }

    override suspend fun setContrast(themeContrast: ThemeContrast) {
        flowSettings.putDouble(THEME_CONTRAST, themeContrast.value)
    }
}

enum class SettingsDarkTheme {
    User,
    ForceLight,
    ForceDark
}

data class ThemeSettings(
    val isSystemDynamic: Boolean = false,
    val settingsDarkTheme: SettingsDarkTheme = User,
    val isAmoled: Boolean = false,
    val seedColor: Color = blue,
    val paletteStyle: PaletteStyle = TonalSpot,
    val isHighFidelity: Boolean = false,
    val contrast: ThemeContrast = default
)
