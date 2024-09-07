package com.timerx.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun SetStatusBarColor(color: Color) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val insetsController = WindowCompat.getInsetsController(window, view)
    insetsController.isAppearanceLightStatusBars = isColorDark(color).not()
    insetsController.isAppearanceLightNavigationBars = isColorDark(color).not()
}
