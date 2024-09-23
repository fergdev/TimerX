package com.timerx.ui.settings.main

import com.timerx.settings.TimerXSettings
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

internal class MainSettingsContainer(
    private val settings: TimerXSettings
) : Container<MainSettingsState, MainSettingsIntent, Nothing> {
    override val store = store(MainSettingsState()) {
        whileSubscribed {
            settings.keepScreenOn.collect { keepScreenOn ->
                updateState {
                    MainSettingsState(keepScreenOn)
                }
            }
        }
        reduce {
            when (it) {
                is MainSettingsIntent.KeepScreenOn -> settings.setKeepScreenOn(it.keepScreenOn)
            }
        }
    }
}