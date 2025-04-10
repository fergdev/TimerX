package com.timerx.ui.settings.background

import com.timerx.settings.BackgroundAlpha
import com.timerx.settings.Pattern
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface BackgroundSettingsState : MVIState {
    data object Loading : BackgroundSettingsState
    data class LoadedState(
        val backgroundAlpha: BackgroundAlpha,
        val pattern: Pattern,
    ) : BackgroundSettingsState
}

interface BackgroundSettingsIntent : MVIIntent {
    data class UpdateAlpha(val backgroundAlpha: BackgroundAlpha) : BackgroundSettingsIntent
    data class UpdatePattern(val pattern: Pattern) : BackgroundSettingsIntent
}
