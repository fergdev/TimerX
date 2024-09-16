package com.timerx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.timerx.ui.common.TimerXTheme
import com.timerx.ui.create.CreateContent
import com.timerx.ui.main.MainContent
import com.timerx.ui.navigation.RootComponent
import com.timerx.ui.run.RunContent
import com.timerx.ui.settings.SettingsContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AppContent(rootComponent: RootComponent) = TimerXTheme {
    val state = rootComponent.stack.subscribeAsState()

    val animation: StackAnimation<Any, RootComponent.Child> = predictiveBackAnimation(
        backHandler = rootComponent.backHandler,
        fallbackAnimation = stackAnimation(fade() + scale()),
        onBack = rootComponent::onBackClicked,
    )
    Children(
        stack = state.value,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        animation = animation
    ) {
        val child = it.instance
        when (child) {
            is RootComponent.Child.CreateChild -> CreateContent(child.component)
            is RootComponent.Child.MainChild -> MainContent(child.component)
            is RootComponent.Child.RunChild -> RunContent(child.component)
            is RootComponent.Child.SettingsChild -> SettingsContent(child.component)
        }
    }
}
