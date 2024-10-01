package com.timerx.ui.settings.main

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias MainSettingsStore = Store<MainSettingsState, MainSettingsIntent, Nothing>

interface MainSettingsComponent : MainSettingsStore {
    fun onBack()
    fun onAlert()
    fun onTheme()
    fun onBackgroundSettings()
    fun onAboutLibs()
}

class DefaultMainSettingsComponent(
    private val backClicked: () -> Unit,
    private val alertClicked: () -> Unit,
    private val themeClicked: () -> Unit,
    private val backgroundSettings: () -> Unit,
    private val aboutLibs: () -> Unit,
    context: ComponentContext,
    factory: () -> MainSettingsContainer
) : ComponentContext by context,
    MainSettingsStore by context.retainedStore(factory = factory),
    MainSettingsComponent {
    override fun onBack() {
        backClicked()
    }

    override fun onAlert() {
        alertClicked()
    }

    override fun onTheme() {
        themeClicked()
    }

    override fun onBackgroundSettings() {
        backgroundSettings()
    }

    override fun onAboutLibs() {
        aboutLibs()
    }
}
