package com.timerx.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.hoc081098.flowext.combine
import com.materialkolor.Contrast
import com.materialkolor.PaletteStyle
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.domain.SortTimersBy
import com.timerx.ui.theme.presetColors
import com.timerx.util.mapIfNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import com.russhwolf.settings.Settings as MPSettings

data class Settings(
    val volume: Float,
    val vibrationEnabled: Boolean,
    val ignoreNotificationsPermissions: Boolean,
    val sortTimersBy: SortTimersBy
)

interface ITimerXSettings {
    val settings: Flow<Settings>

    suspend fun setVolume(volume: Float)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setIgnoreNotificationPermissions()
    suspend fun setSortTimersBy(sortTimersBy: SortTimersBy)

    val themeSettings: Flow<ThemeSettings>
    suspend fun setSeedColor(seedColor: Color)
    suspend fun setPaletteStyle(style: PaletteStyle)
    suspend fun setIsAmoled(notIsAmoled: Boolean)
    suspend fun setDarkTheme(user: SettingsDarkTheme)
    suspend fun setIsDynamicTheme(isDynamic: Boolean)
    suspend fun setIsHighFidelity(isHighFidelity: Boolean)
    suspend fun setContrast(contrast: Double)
}

private const val VIBRATION_ENABLED = "vibrationEnabled"
private const val VOLUME = "volume"
private const val SET_IGNORE_NOTIFICATIONS_PERMISSION = "setIgnoreNotificationsPermission"
private const val SORT_TIMERS_BY = "sortTimersBy"

private const val SEED_COLOR = "seedColor"
private const val PALETTE_STYLE = "paletteStyle"
private const val DARK_THEME = "darkTheme"
private const val DYNAMIC_THEME = "dynamicTheme"
private const val IS_AMOLED = "isAmoled"
private const val IS_HIGH_FIDELITY = "isHighFidelity"
private const val THEME_CONTRAST = "contrast"

@OptIn(ExperimentalSettingsApi::class)
class TimerXSettings : ITimerXSettings {
    private val flowSettings = MPSettings().makeObservable().toFlowSettings(Dispatchers.Main)
    private val volume = flowSettings.getFloatOrNullFlow(VOLUME).mapIfNull(1F)
    private val vibrationEnabled =
        flowSettings.getBooleanOrNullFlow(VIBRATION_ENABLED).mapIfNull(true)
    private val ignoreNotificationsPermissions =
        flowSettings.getBooleanOrNullFlow(SET_IGNORE_NOTIFICATIONS_PERMISSION).mapIfNull(false)
    private val sortTimersBy = flowSettings.getIntOrNullFlow(SORT_TIMERS_BY).mapIfNull(0)

    override val settings: Flow<Settings> = combine(
        volume, vibrationEnabled, ignoreNotificationsPermissions, sortTimersBy
    ) { a, b, c, d ->
        Settings(
            volume = a,
            vibrationEnabled = b,
            ignoreNotificationsPermissions = c,
            sortTimersBy = SortTimersBy.entries[d]
        )
    }

    override suspend fun setVolume(volume: Float) {
        flowSettings.putFloat(VOLUME, volume)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        flowSettings.putBoolean(VIBRATION_ENABLED, enabled)
    }

    override suspend fun setIgnoreNotificationPermissions() {
        flowSettings.putBoolean(SET_IGNORE_NOTIFICATIONS_PERMISSION, true)
    }

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) {
        flowSettings.putInt(SORT_TIMERS_BY, sortTimersBy.ordinal)
    }

    private val isDynamicTheme = flowSettings.getBooleanOrNullFlow(DYNAMIC_THEME).mapIfNull(false)
    private val isSettingsDarkTheme = flowSettings.getIntOrNullFlow(DARK_THEME).map {
        if (it == null) SettingsDarkTheme.User
        else SettingsDarkTheme.entries[it]
    }
    private val isAmoled = flowSettings.getBooleanOrNullFlow(IS_AMOLED).mapIfNull(false)
    private val seedColor =
        flowSettings.getIntOrNullFlow(SEED_COLOR).mapIfNull(presetColors[0].toArgb())
    private val style = flowSettings.getIntOrNullFlow(PALETTE_STYLE).map {
        if (it == null) PaletteStyle.TonalSpot
        else PaletteStyle.entries[it]
    }
    private val isHighFidelity =
        flowSettings.getBooleanOrNullFlow(IS_HIGH_FIDELITY).mapIfNull(false)
    private val contrast = flowSettings.getDoubleOrNullFlow(THEME_CONTRAST).map {
        it ?: Contrast.Default.value
    }

    override val themeSettings = combine(
        isDynamicTheme, isSettingsDarkTheme, isAmoled, seedColor, style, isHighFidelity, contrast
    ) { isDynamicTheme, isDarkTheme, isAmoled, seedColor, style, isHighFidelity, contrast ->
        ThemeSettings(
            isSystemDynamic = isDynamicTheme,
            settingsDarkTheme = isDarkTheme,
            isAmoled = isAmoled,
            seedColor = Color(seedColor),
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
        flowSettings.putBoolean(DYNAMIC_THEME, isDynamic)
    }

    override suspend fun setIsHighFidelity(isHighFidelity: Boolean) {
        flowSettings.putBoolean(IS_HIGH_FIDELITY, isHighFidelity)
    }

    override suspend fun setContrast(contrast: Double) {
        flowSettings.putDouble(THEME_CONTRAST, contrast)
    }
}

enum class SettingsDarkTheme {
    User,
    ForceLight,
    ForceDark
}


data class ThemeSettings(
    val isSystemDynamic: Boolean = false,
    val settingsDarkTheme: SettingsDarkTheme = SettingsDarkTheme.User,
    val isAmoled: Boolean = true,
    val seedColor: Color = presetColors[0],
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val isHighFidelity: Boolean = false,
    val contrast: Double = Contrast.Default.value
)
