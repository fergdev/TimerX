package com.timerx.ui.settings.alerts

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias AlertStore = Store<AlertsSettingsState, AlertsSettingsIntent, Nothing>

interface AlertSettingsComponent : AlertStore {
    fun onBackClicked()
}

class DefaultAlertSettingsComponent(
    private val backClicked: () -> Unit,
    factory: () -> AlertsSettingsContainer,
    context: ComponentContext
) : ComponentContext by context,
    AlertStore by context.retainedStore(factory = factory),
    AlertSettingsComponent {

    override fun onBackClicked() {
        backClicked()
    }
}
