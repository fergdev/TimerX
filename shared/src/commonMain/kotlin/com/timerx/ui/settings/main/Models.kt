package com.timerx.ui.settings.main

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

internal data class MainSettingsState(
    val isKeepScreenOn: Boolean = false
) : MVIState

internal sealed interface MainSettingsIntent : MVIIntent {
    data class KeepScreenOn(val keepScreenOn: Boolean) : MainSettingsIntent
}
