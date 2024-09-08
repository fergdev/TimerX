package com.timerx.ui.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo

internal const val TIMER_ID_OPTIONAL = "{timerId}?"
internal const val TIMER_ID_RAW = "timerId"

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

class NavigationProvider {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    internal val navigator = Navigator()

    // TODO provide back stack for navigation flow
    private val _navigationFlow = MutableStateFlow<Screen>(Screen.MainScreen)
    val navigationFlow: StateFlow<Screen?> = _navigationFlow

    fun navigateTo(screen: Screen) {
        coroutineScope.launch {
            if (navigationFlow.value is Screen.RunScreen) {
                if (navigator.canGoBack.first()) {
                    navigator.goBack()
                }

                // Delay required to prevent timing issues with
                // starting notification service.
                delay(100)
            }
            val options = if (screen == Screen.MainScreen) {
                NavOptions(
                    launchSingleTop = true,
                    popUpTo = PopUpTo("", true)
                )
            } else null
            doNavigation(screen, options)
        }
    }

    private fun doNavigation(screen: Screen, options: NavOptions? = null) {
        _navigationFlow.value = screen
        navigator.navigate(screen.routeWithParams, options)
    }

    fun goBack() {
        coroutineScope.launch {
            if (navigator.canGoBack.first()) {
                navigator.goBack()
            } else {
                navigateTo(Screen.MainScreen)
            }
        }
    }
}
