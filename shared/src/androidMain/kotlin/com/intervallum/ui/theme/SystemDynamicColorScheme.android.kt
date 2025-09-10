package com.intervallum.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.intervallum.platform.supportsDynamicColors

@Composable
internal actual fun systemDynamicColorScheme(dark: Boolean): ColorScheme? {
    val context = LocalContext.current
    return remember(dark) {
        when {
            supportsDynamicColors && dark -> dynamicDarkColorScheme(context).copy()
            supportsDynamicColors && !dark -> dynamicLightColorScheme(context).copy()
            else -> null
        }
    }
}
