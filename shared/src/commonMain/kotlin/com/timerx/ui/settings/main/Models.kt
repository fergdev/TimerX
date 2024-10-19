package com.timerx.ui.settings.main

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

data class MainSettingsState(
    val isKeepScreenOn: Boolean = false,
    val privacyPolicyUri: String,
) : MVIState

sealed interface MainSettingsIntent : MVIIntent {
    data class KeepScreenOn(val keepScreenOn: Boolean) : MainSettingsIntent
}
