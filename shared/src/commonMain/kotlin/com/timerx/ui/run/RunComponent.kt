package com.timerx.ui.run

import com.arkivanov.decompose.ComponentContext
import com.timerx.ui.navigation.RunComponent

class DefaultRunComponent(
    componentContext: ComponentContext,
    override val timerId: Long,
    private val onBack: () -> Unit
) : RunComponent {
    override fun onBackClicked() {
        onBack()
    }
}