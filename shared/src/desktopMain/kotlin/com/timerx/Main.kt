package com.timerx

import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.KoinContext

fun main() = application {
//    startKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "TimerX"
    ) {
        KoinContext { Text(text = "wowoww") }
    }
}
