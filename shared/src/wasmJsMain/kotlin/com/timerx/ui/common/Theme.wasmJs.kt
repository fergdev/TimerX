package com.timerx.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberColorScheme(
    dark: Boolean,
    dynamic: Boolean
) = remember{if(dark) darkColorScheme else lightColorScheme}
