package com.timerx.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.koin.compose.KoinApplication

@Composable
fun App() {
    PreComposeApp {
        KoinApplication(
            application = { modules(sharedModule()) }
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
                    scene(
                        route = "create/{timerId}?",
                        navTransition = NavTransition(
                            createTransition = fadeIn() + slideInVertically { it / 2 },
                            destroyTransition = fadeOut() + slideOutVertically { it / 2 }
                        )
                    ) {
                        CreateScreen(
                            timerId = it.path<String>("timerId") ?: ""
                        ) { navigator.goBack() }
                    }
                    scene(
                        route = "run/{timerId}",
                        navTransition = NavTransition(
                            createTransition = fadeIn() + slideInHorizontally { it / 2 },
                            destroyTransition = fadeOut() + slideOutHorizontally { it / 2 }
                        )
                    ) {
                        RunScreen(it.path<String>("timerId")!!) { navigator.goBack() }
                    }
                    scene(
                        route = "settings",
                        navTransition = NavTransition(
                            createTransition = fadeIn() + slideInVertically(),
                            destroyTransition = fadeOut() + slideOutVertically()
                        )
                    ) {
                        SettingsScreen { navigator.goBack() }
                    }
                }
            }
        }
    }
}