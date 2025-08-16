package com.intervallum.ui.settings.background

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias BackgroundSettingsStore = Store<BackgroundSettingsState, BackgroundSettingsIntent, Nothing>

interface BackgroundSettingsComponent : BackgroundSettingsStore {
    fun onBackClicked()
}

class DefaultBackgroundSettingsComponent(
    private val backClicked: () -> Unit,
    factory: () -> BackgroundSettingsContainer,
    context: ComponentContext
) : ComponentContext by context,
    BackgroundSettingsStore by context.retainedStore(factory = factory),
    BackgroundSettingsComponent {

    override fun onBackClicked() {
        backClicked()
    }
}
