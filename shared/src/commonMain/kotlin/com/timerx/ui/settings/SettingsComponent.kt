package com.timerx.ui.settings

import com.arkivanov.decompose.ComponentContext

interface SettingsComponent {
    fun onBackClicked()
}

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit
) :
    SettingsComponent {
    override fun onBackClicked() {
        onBack()
    }
}