package com.timerx.ui.main

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias MainStore = Store<MainState, MainIntent, MainAction>

interface MainComponent : MainStore {
    fun onSettingsClicked()
    fun onCreateClicked(timerId: Long? = null)
    fun onRunClicked(id: Long)
}

@Suppress("UnusedPrivateProperty")
internal class DefaultMainComponent(
    componentContext: ComponentContext,
    factory: () -> MainContainer,
    private val onRunTimer: (Long) -> Unit,
    private val onSettings: () -> Unit,
    private val onCreate: (Long?) -> Unit
) : ComponentContext by componentContext,
    MainStore by componentContext.retainedStore(factory = factory),
    MainComponent {

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
