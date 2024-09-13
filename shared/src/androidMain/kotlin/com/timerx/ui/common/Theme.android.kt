package com.timerx.ui.common

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.timerx.util.supportsDynamicColors

@Composable
internal actual fun rememberColorScheme(
    dark: Boolean,
    dynamic: Boolean
): ColorScheme {
    val context = LocalContext.current
    return remember(dark, dynamic) {
        val dynamicColors = supportsDynamicColors && dynamic
        when {
            dynamicColors && dark -> dynamicDarkColorScheme(context).copy(scrim = md_theme_dark_scrim)
            dynamicColors && !dark -> dynamicLightColorScheme(context).copy(scrim = md_theme_light_scrim)
            !dynamicColors && dark -> darkColorScheme
            else -> lightColorScheme // !dynamicColors && !useDarkTheme
        }
    }
}