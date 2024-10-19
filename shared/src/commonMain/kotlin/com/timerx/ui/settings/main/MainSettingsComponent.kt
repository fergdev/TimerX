package com.timerx.ui.settings.main

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias MainSettingsStore = Store<MainSettingsState, MainSettingsIntent, Nothing>

interface MainSettingsComponent : MainSettingsStore {
    fun onBackClicked()
    fun onAlertsClicked()
    fun onThemeClicked()
    fun onBackgroundClicked()
    fun onAboutClicked()
}

class DefaultMainSettingsComponent(
    private val backClicked: () -> Unit,
    private val alertClicked: () -> Unit,
    private val themeClicked: () -> Unit,
    private val backgroundClicked: () -> Unit,
    private val aboutClicked: () -> Unit,
    context: ComponentContext,
    factory: () -> MainSettingsContainer
) : ComponentContext by context,
    MainSettingsStore by context.retainedStore(factory = factory),
    MainSettingsComponent {
    override fun onBackClicked() {
        backClicked()
    }

    override fun onAlertsClicked() {
        alertClicked()
    }

    override fun onThemeClicked() {
        themeClicked()
    }

    override fun onBackgroundClicked() {
        backgroundClicked()
    }

    override fun onAboutClicked() {
        aboutClicked()
    }
}
