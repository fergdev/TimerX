package com.timerx.ui.settings.theme

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias ThemeStore = Store<ThemeSettingsState, ThemeSettingsIntent, Nothing>

interface ThemeSettingsComponent: ThemeStore {
    fun onBackClicked()
}

class DefaultThemeSettingsComponent(
    private val backClicked: () -> Unit,
    factory: () -> ThemeSettingsContainer,
    context: ComponentContext
) : ComponentContext by context,
    ThemeStore by context.retainedStore(factory = factory),
    ThemeSettingsComponent {
    override fun onBackClicked() {
        backClicked()
    }
}
