package com.timerx.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.timerx.ui.common.isColorDark

@Composable
actual fun SetStatusBarColor(color: Color) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    window.statusBarColor = color.toArgb()
    window.navigationBarColor = color.toArgb()
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
        isColorDark(color).not()
    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
        isColorDark(color).not()
}
