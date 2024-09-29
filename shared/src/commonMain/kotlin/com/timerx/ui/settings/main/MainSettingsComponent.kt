package com.timerx.ui.settings.main

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias MainSettingsStore = Store<MainSettingsState, MainSettingsIntent, Nothing>

interface MainSettingsComponent: MainSettingsStore {
    fun onBackClicked()
    fun onAlertClicked()
    fun onThemeClicked()
}

class DefaultMainSettingsComponent(
    private val backClicked: () -> Unit,
    private val alertClicked: () -> Unit,
    private val themeClicked: () -> Unit,
    context: ComponentContext,
    factory: () -> MainSettingsContainer
) : ComponentContext by context,
    MainSettingsStore by context.retainedStore(factory = factory),
    MainSettingsComponent {
    override fun onBackClicked() {
        backClicked()
    }

    override fun onAlertClicked() {
        alertClicked()
    }

    override fun onThemeClicked() {
        themeClicked()
    }
}
