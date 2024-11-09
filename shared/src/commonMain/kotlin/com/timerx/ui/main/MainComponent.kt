package com.timerx.ui.main

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias MainStore = Store<MainState, MainIntent, MainAction>

interface MainComponent : MainStore {
    val onSettings: () -> Unit
    val onCreate: (timerId: Long?) -> Unit
    val onRun: (id: Long) -> Unit
}

@Suppress("UnusedPrivateProperty")
internal class DefaultMainComponent(
    componentContext: ComponentContext,
    factory: () -> MainContainer,
    override val onRun: (Long) -> Unit,
    override val onSettings: () -> Unit,
    override val onCreate: (Long?) -> Unit
) : ComponentContext by componentContext,
    MainStore by componentContext.retainedStore(factory = factory),
    MainComponent
