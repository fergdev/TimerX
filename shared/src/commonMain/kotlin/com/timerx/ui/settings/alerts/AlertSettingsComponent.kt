package com.timerx.ui.settings.alerts

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias AlertStore = Store<AlertsSettingsState, AlertsSettingsIntent, Nothing>

interface AlertSettingsComponent : AlertStore {
    val onBackClicked: () -> Unit
}

class DefaultAlertSettingsComponent(
    override val onBackClicked: () -> Unit,
    factory: () -> Container<AlertsSettingsState, AlertsSettingsIntent, Nothing>,
    context: ComponentContext
) : ComponentContext by context,
    AlertStore by context.retainedStore(factory = factory),
    AlertSettingsComponent
