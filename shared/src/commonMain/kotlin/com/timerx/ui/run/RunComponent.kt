package com.timerx.ui.run

import com.arkivanov.decompose.ComponentContext

interface RunComponent {
    val timerId: Long
    fun onBackClicked()
}

class DefaultRunComponent(
    componentContext: ComponentContext,
    override val timerId: Long,
    private val onBack: () -> Unit
) : RunComponent {
    override fun onBackClicked() {
        onBack()
    }
}
