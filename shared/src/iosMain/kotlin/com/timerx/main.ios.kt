package com.timerx

import com.timerx.ui.App
import moe.tlaster.precompose.PreComposeApplication
import org.koin.compose.KoinApplication

@Suppress("FunctionName")
fun MainViewController() = PreComposeApplication {
    KoinApplication(
        application = {
            modules(sharedModule())
        }
    ) {
        TimerXTheme {
            App()
        }
    }
}