package com.timerx.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.DynamicMaterialThemeState
import com.materialkolor.rememberDynamicMaterialThemeState
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.SettingsDarkTheme
import com.timerx.settings.ThemeSettings
import com.timerx.ui.theme.systemDynamicColorScheme
import org.koin.compose.koinInject

private val typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
private val shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TimerXTheme(content: @Composable () -> Unit) {
    val settings = koinInject<ITimerXSettings>().themeSettingsManager.themeSettings.collectAsState(ThemeSettings())
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
    val state = rememberDynamicMaterialThemeState(
        seedColor = settings.value.seedColor,
        isDark = isDark,
        isAmoled = settings.value.isAmoled,
        style = settings.value.paletteStyle,
        extendedFidelity = settings.value.isHighFidelity,
        contrastLevel = settings.value.contrast,
        modifyColorScheme = modifyColorScheme
    )
    DynamicMaterialTheme(
        state = state,
        animate = true,
        typography = typography,
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
