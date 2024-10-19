package com.timerx.ui.settings.main

import com.timerx.BuildFlags
import com.timerx.settings.TimerXSettings
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class MainSettingsContainer(
    private val settings: TimerXSettings
) : Container<MainSettingsState, MainSettingsIntent, Nothing> {
    override val store = store(MainSettingsState(privacyPolicyUri = BuildFlags.privacyPolicyUrl)) {
        whileSubscribed {
            settings.keepScreenOn.collect { keepScreenOn ->
                updateState { copy(isKeepScreenOn = keepScreenOn) }
            }
        }
        reduce {
            when (it) {
                is MainSettingsIntent.KeepScreenOn -> settings.setKeepScreenOn(it.keepScreenOn)
            }
        }
    }
}
