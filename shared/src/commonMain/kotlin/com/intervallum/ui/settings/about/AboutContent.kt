package com.intervallum.ui.settings.about

import androidx.compose.foundation.layout.fillMaxSize
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
import com.intervallum.ui.settings.about.main.AboutMainContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun AboutContent(component: AboutComponent) {
    val state = component.stack.subscribeAsState()

    val animation: StackAnimation<Any, AboutComponent.Child> = predictiveBackAnimation(
        backHandler = component.backHandler,
        fallbackAnimation = stackAnimation(fade() + scale()),
        onBack = component::onBackClicked,
    )
    val value = state.value
    Children(
        stack = value,
        modifier = Modifier.fillMaxSize(),
        animation = animation
    ) {
        val child = it.instance
        when (child) {
            is AboutComponent.Child.Main -> AboutMainContent(child.component)
        }
    }
}
