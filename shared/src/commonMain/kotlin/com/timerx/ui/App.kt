package com.timerx.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.timerx.ui.common.TimerXTheme
import com.timerx.ui.create.CreateScreen
import com.timerx.ui.main.MainScreen
import com.timerx.ui.run.RunScreen
import com.timerx.ui.settings.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.koin.mp.KoinPlatform

private const val TIMER_ID_OPTIONAL = "{timerId}?"
private const val TIMER_ID_RAW = "timerId"

sealed interface Screen {
    val routeWithParams: String

    data object MainScreen : Screen {
        const val ROUTE = "main"
        override val routeWithParams = ROUTE
    }

    data object SettingsScreen : Screen {
        const val ROUTE = "settings"
        override val routeWithParams = ROUTE
    }

    class CreateScreen(private val id: Long? = null) : Screen {
        override val routeWithParams
            get() =
                if (id == null) ROUTE.replace("/$TIMER_ID_OPTIONAL", "")
                else ROUTE.replace(TIMER_ID_OPTIONAL, id.toString())

        companion object {
            const val ROUTE = "create/{timerId}?"
        }
    }

    class RunScreen(private val id: Long) : Screen {
        override val routeWithParams
            get() =
                ROUTE.replace(TIMER_ID_OPTIONAL, id.toString())

        companion object {
            const val ROUTE = "run/{timerId}?"
        }
    }
}

@Composable
fun App() {
    PreComposeApp {
        TimerXTheme {
            val navigator = rememberNavigator()
            Surface(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navigator = navigator,
                    initialRoute = Screen.MainScreen.ROUTE
                ) {
                    scene(route = Screen.MainScreen.ROUTE) {
                        MainScreen {
                            navigator.navigate(it.routeWithParams)
                        }
                    }
                    scene(
                        route = Screen.CreateScreen.ROUTE,
                        navTransition = NavTransition(
                            createTransition = slideInVertically { it / 2 },
                            destroyTransition = slideOutVertically { it / 2 }
                        )
                    ) {
                        CreateScreen(
                            timerId = it.path<String>(TIMER_ID_RAW) ?: ""
                        ) { navigator.goBack() }
                    }
                    scene(
                        route = Screen.RunScreen.ROUTE,
                        navTransition = NavTransition(
                            createTransition = slideInHorizontally { it / 2 },
                            destroyTransition = slideOutHorizontally { it / 2 }
                        )
                    ) {
                        RunScreen(it.path<String>(TIMER_ID_RAW)!!) {
                            navigator.goBack()
                        }
                    }
                    scene(
                        route = Screen.SettingsScreen.ROUTE,
                        navTransition = NavTransition(
                            createTransition = slideInVertically(),
                            destroyTransition = slideOutVertically()
                        )
                    ) {
                        SettingsScreen { navigator.goBack() }
                    }
                }
                val coroutineScope = rememberCoroutineScope()
                val screen =
                    KoinPlatform.getKoin().get<NavigationProvider>().navigationFlow.collectAsState()
                screen.value?.let {
                    coroutineScope.launch {
                        val route = navigator.currentEntry.first()
                        route?.let {
                            navigator.goBack()
                            // Delay required to prevent timing issues with
                            // starting notification service.
                            delay(100)
                        }
                        navigator.navigate(it.routeWithParams)
                    }
                }
            }
        }
    }
}
