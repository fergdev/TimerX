@file:Suppress("Filename")

package com.timerx

import androidx.compose.ui.window.ComposeUIViewController
import com.timerx.di.startKoin
import com.timerx.ui.App

@Suppress("FunctionName")
fun MainViewController() = ComposeUIViewController {
    startKoin()
    App()
}
