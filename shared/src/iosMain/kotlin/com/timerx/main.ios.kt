package com.timerx

import androidx.compose.ui.window.ComposeUIViewController
import com.timerx.ui.App
import org.koin.core.context.startKoin

@Suppress("FunctionName")
fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
fun initKoin(){
    startKoin {
        modules(appModule())
    }
}
