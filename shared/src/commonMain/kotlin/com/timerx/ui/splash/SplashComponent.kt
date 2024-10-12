package com.timerx.ui.splash

interface SplashComponent {
    fun finishSplash()
}

internal class DefaultSplashComponent(
    private val onFinished: () -> Unit
) : SplashComponent {
    override fun finishSplash() {
        onFinished()
    }
}
