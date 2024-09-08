package com.timerx.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.timerx.ui.common.TimerXTheme
import com.timerx.ui.navigation.AppNavigation
import moe.tlaster.precompose.PreComposeApp

@Composable
fun App() {
    PreComposeApp {
        TimerXTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                AppNavigation()
            }
        }
    }
}
