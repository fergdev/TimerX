package com.intervallum.ui.run

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias RunStore = Store<RunScreenState, RunScreenIntent, RunAction>

interface RunComponent : RunStore {
    val onBack: () -> Unit
}

@Suppress("UnusedPrivateProperty")
internal class DefaultRunComponent(
    componentContext: ComponentContext,
    factory: () -> RunContainer,
    override val onBack: () -> Unit
) : ComponentContext by componentContext,
    RunStore by componentContext.retainedStore(factory = factory),
    RunComponent
