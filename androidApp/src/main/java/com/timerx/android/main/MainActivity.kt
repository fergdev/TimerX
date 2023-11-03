package com.timerx.android.main

import android.os.Bundle
import com.timerx.TimerXTheme
import com.timerx.sharedModule
import com.timerx.ui.App
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import org.koin.compose.KoinApplication

class MainActivity : PreComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
    }
}