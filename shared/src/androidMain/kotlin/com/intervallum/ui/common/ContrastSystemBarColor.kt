package com.intervallum.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import org.koin.mp.KoinPlatform

actual fun contrastSystemBarColor(color: Color) {
    val activity = KoinPlatform.getKoin().get<ComponentActivity>()
    val window = activity.window
    val view = window.decorView
    val insetsController = WindowCompat.getInsetsController(window, view)
    insetsController.isAppearanceLightStatusBars = isColorDark(color).not()
    insetsController.isAppearanceLightNavigationBars = isColorDark(color).not()
}
