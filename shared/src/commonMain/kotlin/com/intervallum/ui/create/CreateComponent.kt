package com.intervallum.ui.create

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias CreateStore = Store<CreateScreenState, CreateScreenIntent, CreateAction>

interface CreateComponent : CreateStore {
    fun onBackClicked()
    fun onTimerUpdated(timerId: Long)
}

class DefaultCreateComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
    private val timerUpdated: (Long) -> Unit,
    factory: () -> CreateContainer
) : ComponentContext by componentContext,
    CreateStore by componentContext.retainedStore(factory = factory),
    CreateComponent {

    override fun onBackClicked() {
        onBack()
    }

    override fun onTimerUpdated(timerId: Long) {
        timerUpdated(timerId)
    }
}
