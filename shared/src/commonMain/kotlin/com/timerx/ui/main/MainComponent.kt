package com.timerx.ui.main

import com.arkivanov.decompose.ComponentContext

interface MainComponent {
    fun onSettingsClicked()
    fun onCreateClicked(timerId: Long? = null)
    fun onRunClicked(id: Long)
}

@Suppress("UnusedPrivateProperty")
class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onRunTimer: (Long) -> Unit,
    private val onSettings: () -> Unit,
    private val onCreate: (Long?) -> Unit
) : MainComponent {
    override fun onSettingsClicked() {
        onSettings()
    }

    override fun onCreateClicked(timerId: Long?) {
        onCreate(timerId)
    }

    override fun onRunClicked(id: Long) {
        onRunTimer(id)
    }
}
