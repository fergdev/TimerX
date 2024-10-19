package com.timerx.ui.settings

import androidx.compose.foundation.layout.widthIn
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
import com.timerx.ui.settings.about.AboutContent
import com.timerx.ui.settings.alerts.AlertsSettingsContent
import com.timerx.ui.settings.background.BackgroundSettingsContent
import com.timerx.ui.settings.main.MainSettingsContent
import com.timerx.ui.settings.theme.ThemeSettingsContent
import com.timerx.ui.theme.Size

@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun SettingsContent(settingsComponent: SettingsComponent) {
    val state = settingsComponent.stack.subscribeAsState()

    val animation: StackAnimation<Any, SettingsComponent.Child> = predictiveBackAnimation(
        backHandler = settingsComponent.backHandler,
        fallbackAnimation = stackAnimation(fade() + scale()),
        onBack = settingsComponent::onBackClicked,
    )
    Children(
        stack = state.value,
        modifier = Modifier.widthIn(Size.maxWidth),
        animation = animation
    ) {
        val child = it.instance
        when (child) {
            is SettingsComponent.Child.Main -> MainSettingsContent(child.component)
            is SettingsComponent.Child.Alerts -> AlertsSettingsContent(child.component)
            is SettingsComponent.Child.Theme -> ThemeSettingsContent(child.component)
            is SettingsComponent.Child.Background -> BackgroundSettingsContent(child.component)
            is SettingsComponent.Child.About -> AboutContent(child.component)
        }
    }
}
