package com.timerx.ui.settings.main

import com.timerx.BuildFlags
import com.timerx.settings.TimerXSettings
import com.timerx.ui.di.ConfigurationFactory
import com.timerx.ui.di.configure
import com.timerx.ui.settings.main.MainSettingsIntent.KeepScreenOn
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class MainSettingsContainer(
    configurationFactory: ConfigurationFactory,
    private val settings: TimerXSettings
) : Container<MainSettingsState, MainSettingsIntent, Nothing> {
    override val store = store(MainSettingsState(privacyPolicyUri = BuildFlags.privacyPolicyUrl)) {
        configure(configurationFactory, "Settings:Main")
        whileSubscribed {
            settings.keepScreenOn.collect { keepScreenOn ->
                updateState { copy(isKeepScreenOn = keepScreenOn) }
            }
        }
        reduce {
            when (it) {
                is KeepScreenOn -> settings.setKeepScreenOn(it.keepScreenOn)
            }
        }
    }
}
