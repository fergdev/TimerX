package com.timerx

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.timerx.di.startKoin
import com.timerx.ui.AppContent
import com.timerx.ui.navigation.DefaultRootComponent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.app_name

@OptIn(ExperimentalDecomposeApi::class)
fun main() = application {
    startKoin()
    val lifecycle = LifecycleRegistry()

    val state = rememberWindowState(
        width = 1600.dp,
        height = 1000.dp,
        position = WindowPosition.Aligned(Alignment.Center),
    )
    LifecycleController(lifecycle, state)
    val component = DefaultRootComponent(
        componentContext = DefaultComponentContext(
            lifecycle = lifecycle,
        )
    )
    Window(
        onCloseRequest = ::exitApplication,
//        icon = painterResource(Res.drawable.),
        title = stringResource(Res.string.app_name),
        state = state,
    ) {
        KoinContext { AppContent(component) }
    }
}
