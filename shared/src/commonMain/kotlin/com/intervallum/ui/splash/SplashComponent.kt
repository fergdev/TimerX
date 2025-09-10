package com.intervallum.ui.splash

interface SplashComponent {
    val finishSplash: () -> Unit
}

@Suppress("UseDataClass")
internal class DefaultSplashComponent(
    override val finishSplash: () -> Unit
) : SplashComponent
