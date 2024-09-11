package com.timerx.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.timerx.ui.create.CreateScreen
import com.timerx.ui.main.MainScreen
import com.timerx.ui.run.RunScreen
import com.timerx.ui.settings.SettingsScreen
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.stateholder.currentLocalStateHolder
import org.koin.mp.KoinPlatform

private const val KEY_NAVIGATOR = "Navigator"

@Composable
internal fun AppNavigation() {
    val navigationProvider = remember {
        KoinPlatform.getKoin().get<NavigationProvider>()
    }
    val stateHolder = currentLocalStateHolder
    stateHolder.getOrPut(KEY_NAVIGATOR) {
        navigationProvider.navigator
    }

    NavHost(
        navigator = navigationProvider.navigator,
        initialRoute = Screen.MainScreen.ROUTE
    ) {
        scene(route = Screen.MainScreen.ROUTE) {
            MainScreen { navigationProvider.navigateTo(it) }
        }
        scene(
            route = Screen.CreateScreen.ROUTE,
            navTransition = NavTransition(
                createTransition = slideInVertically { it / 2 },
                destroyTransition = slideOutVertically { it / 2 }
            )
        ) {
            CreateScreen(
                timerId = it.path<Long>(TIMER_ID_RAW) ?: -1L
            ) { navigationProvider.goBack() }
        }
        scene(
            route = Screen.RunScreen.ROUTE,
            navTransition = NavTransition(
                createTransition = slideInHorizontally { it / 2 },
                destroyTransition = slideOutHorizontally { it / 2 }
            )
        ) {
            RunScreen(it.path<Long>(TIMER_ID_RAW)!!) {
                navigationProvider.goBack()
            }
        }
        scene(
            route = Screen.SettingsScreen.ROUTE,
            navTransition = NavTransition(
                createTransition = slideInVertically(),
                destroyTransition = slideOutVertically()
            )
        ) {
            SettingsScreen { navigationProvider.goBack() }
        }
    }
}
