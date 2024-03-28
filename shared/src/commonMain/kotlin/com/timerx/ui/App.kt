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

private const val TIMER_ID_OPTIONAL = "{timerId}?"
private const val TIMER_ID_RAW = "timerId"
private const val TIMER_ID = "{timerId}"

sealed class Screen(val route: String) {
    data object MainScreen : Screen("main")
    data object SettingsScreen : Screen("settings")
    data object AddScreen : Screen("create/{timerId}?") {

        fun addScreen(): String {
            return route.replace("/$TIMER_ID_OPTIONAL", "")
        }

        fun editScreen(id: String): String {
            return route.replace(TIMER_ID_OPTIONAL, id)
        }
    }

    data object RunScreen : Screen("run/{timerId}") {
        fun withParam(id: String): String {
            return route.replace(TIMER_ID, id)
        }
    }
}

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
                    initialRoute = Screen.MainScreen.route
                ) {
                    scene(route = Screen.MainScreen.route) {
                        MainScreen(
                            navigateSettingsScreen = {
                                navigator.navigate(Screen.SettingsScreen.route)
                            }, navigateAddScreen = {
                                navigator.navigate(Screen.AddScreen.addScreen())
                            }, navigateEditScreen = {
                                navigator.navigate(Screen.AddScreen.editScreen(it))
                            }, navigateRunScreen = {
                                navigator.navigate(Screen.RunScreen.withParam(it))
                            })
                    }
                    scene(
                        route = Screen.AddScreen.route,
                        navTransition = NavTransition(
                            createTransition = fadeIn() + slideInVertically { it / 2 },
                            destroyTransition = fadeOut() + slideOutVertically { it / 2 }
                        )
                    ) {
                        CreateScreen(
                            timerId = it.path<String>(TIMER_ID_OPTIONAL) ?: ""
                        ) { navigator.goBack() }
                    }
                    scene(
                        route = Screen.RunScreen.route,
                        navTransition = NavTransition(
                            createTransition = fadeIn() + slideInHorizontally { it / 2 },
                            destroyTransition = fadeOut() + slideOutHorizontally { it / 2 }
                        )
                    ) {
                        RunScreen(it.path<String>(TIMER_ID_RAW)!!) { navigator.goBack() }
                    }
                    scene(
                        route = Screen.SettingsScreen.route,
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
