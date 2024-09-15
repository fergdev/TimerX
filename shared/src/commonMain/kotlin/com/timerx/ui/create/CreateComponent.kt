package com.timerx.ui.create

import com.arkivanov.decompose.ComponentContext

interface CreateComponent {
    val timerId: Long
    fun onBackClicked()
}

class DefaultCreateComponent(
    componentContext: ComponentContext,
    override val timerId: Long,
    private val onBack: () -> Unit
) : CreateComponent {
    override fun onBackClicked() {
        onBack()
    }
}