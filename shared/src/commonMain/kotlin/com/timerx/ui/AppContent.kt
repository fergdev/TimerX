package com.timerx.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.timerx.analytics.TimerXAnalytics
import com.timerx.ui.common.TBackground
import com.timerx.ui.create.CreateContent
import com.timerx.ui.logging.LocalTimerXAnalytics
import com.timerx.ui.main.MainContent
import com.timerx.ui.navigation.RootComponent
import com.timerx.ui.run.RunContent
import com.timerx.ui.settings.SettingsContent
import com.timerx.ui.splash.SplashContent
import com.timerx.ui.theme.TimerXTheme
import org.koin.mp.KoinPlatform

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AppContent(rootComponent: RootComponent) = TimerXTheme {
    CompositionLocalProvider(
        LocalTimerXAnalytics provides KoinPlatform.getKoin().get<TimerXAnalytics>()
    ) {
        TBackground()
        val state = rootComponent.stack.subscribeAsState()
        val animation: StackAnimation<Any, RootComponent.Child> = predictiveBackAnimation(
            backHandler = rootComponent.backHandler,
            fallbackAnimation = stackAnimation(fade() + scale()),
            onBack = rootComponent::onBackClicked,
        )
        Children(
            stack = state.value,
            modifier = Modifier.fillMaxSize(),
            animation = animation
        ) {
            val child = it.instance
            when (child) {
                is RootComponent.Child.CreateChild -> CreateContent(child.component)
                is RootComponent.Child.MainChild -> MainContent(child.component)
                is RootComponent.Child.RunChild -> RunContent(child.component)
                is RootComponent.Child.SettingsChild -> SettingsContent(child.component)
                is RootComponent.Child.SplashChild -> SplashContent(child.component)
            }
        }
    }
}
