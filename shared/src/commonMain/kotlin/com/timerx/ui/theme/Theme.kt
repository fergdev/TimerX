package com.timerx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.DynamicMaterialThemeState
import com.materialkolor.rememberDynamicMaterialThemeState
import com.timerx.settings.SettingsDarkTheme
import com.timerx.settings.ThemeSettings
import com.timerx.settings.TimerXSettings
import org.koin.compose.koinInject

private val shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TimerXTheme(content: @Composable () -> Unit) {
    val settings = koinInject<TimerXSettings>()
        .themeSettingsManager
        .themeSettings
        .collectAsState(ThemeSettings())

    val isDark = isDarkTheme(settings.value.settingsDarkTheme)
    val dynamic = systemDynamicColorScheme(isDark)
    // Required to regenerate colorscheme when the dynamic color has changed
    // returning a different lambdas forces the Dynamic material them to recompose with animation
    // to dynamic
    val modifyColorScheme: (DynamicMaterialThemeState.(ColorScheme) -> ColorScheme) =
        if (settings.value.isSystemDynamic && dynamic != null) {
            { dynamic }
        } else {
            { it }
        }
    val state = with(settings.value) {
        rememberDynamicMaterialThemeState(
            seedColor = seedColor,
            isDark = isDark,
            isAmoled = isAmoled,
            style = paletteStyle,
            extendedFidelity = isHighFidelity,
            contrastLevel = contrast.value,
            modifyColorScheme = modifyColorScheme
        )
    }
    DynamicMaterialTheme(
        state = state,
        animate = true,
        typography = AppTypography,
        shapes = shapes,
        content = {
            Surface(content = content)
        }
    )
}

@Composable
fun isDarkTheme(settingsDarkTheme: SettingsDarkTheme): Boolean {
    val isDark = when (settingsDarkTheme) {
        SettingsDarkTheme.User -> isSystemInDarkTheme()
        SettingsDarkTheme.ForceLight -> false
        SettingsDarkTheme.ForceDark -> true
    }
    return isDark
}
