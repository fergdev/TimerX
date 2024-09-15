package com.timerx.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.timerx.ui.common.TimerXTheme
import com.timerx.ui.navigation.AppNavigation
import com.timerx.ui.navigation.RootComponent

@Composable
fun AppContent(rootComponent: RootComponent) {
    TimerXTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation(rootComponent)
        }
    }
}
