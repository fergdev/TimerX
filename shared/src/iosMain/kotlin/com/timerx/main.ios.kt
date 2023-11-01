package com.timerx

import com.timerx.ui.App
import moe.tlaster.precompose.PreComposeApplication
import org.koin.compose.KoinApplication

@Suppress("FunctionName")
<<<<<<< HEAD
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
=======
fun MainViewController() = ComposeUIViewController {
    Text(text = "Here we are in iOS land!")
    App()
>>>>>>> 38d1bf9 (Initial kmp commit.)
}