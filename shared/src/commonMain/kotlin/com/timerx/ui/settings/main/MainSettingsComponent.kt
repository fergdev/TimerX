package com.timerx.ui.settings.main

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias MainSettingsStore = Store<MainSettingsState, MainSettingsIntent, Nothing>

interface MainSettingsComponent : MainSettingsStore {
    val onBackClicked: () -> Unit
    val onAlertsClicked: () -> Unit
    val onThemeClicked: () -> Unit
    val onBackgroundClicked: () -> Unit
    val onAboutClicked: () -> Unit
}

class DefaultMainSettingsComponent(
    override val onBackClicked: () -> Unit,
    override val onAlertsClicked: () -> Unit,
    override val onThemeClicked: () -> Unit,
    override val onBackgroundClicked: () -> Unit,
    override val onAboutClicked: () -> Unit,
    context: ComponentContext,
    factory: () -> MainSettingsContainer
) : ComponentContext by context,
    MainSettingsStore by context.retainedStore(factory = factory),
    MainSettingsComponent
