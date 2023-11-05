package com.timerx.ui

import androidx.compose.runtime.Composable
import com.timerx.TimerXTheme
import com.timerx.sharedModule
import com.timerx.ui.run.RunScreen
import com.timerx.ui.settings.SettingsScreen
import com.timerx.ui.create.CreateScreen
import com.timerx.ui.main.MainScreen
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.KoinApplication

@Composable
fun App() {
    PreComposeApp {
        KoinApplication(
            application = {
                modules(sharedModule())
            }
        ) {
            TimerXTheme {
                val navigator = rememberNavigator()
                NavHost(
                    navigator = navigator,
                    initialRoute = "main"
                ) {
                    scene(route = "main") {
                        MainScreen(
                            navigateSettingsScreen = {
                                navigator.navigate("settings")
                            }, navigateAddScreen = {
                                navigator.navigate("create")
                            }, navigateEditScreen = {
                                navigator.navigate("create/$it")
                            }, navigateRunScreen = {
                                navigator.navigate("run/$it")
                            })
                    }
                    scene(route = "create/{timerId}?") {
                        val timerId: Long = it.path<Long>("timerId") ?: -1
                        CreateScreen(timerId = timerId) { navigator.goBack() }
                    }
                    scene(route = "run/{timerId}") {
                        val timerId: Long = it.path<Long>("timerId")!!
                        RunScreen(timerId) { navigator.goBack() }
                    }
                    scene(route = "settings") {
                        SettingsScreen { navigator.goBack() }
                    }
                }
            }
        }
    }
}