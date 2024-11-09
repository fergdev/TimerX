package com.timerx.ui.settings.background

import com.timerx.settings.BackgroundSettingsManager
import com.timerx.ui.di.ConfigurationFactory
import com.timerx.ui.di.configure
import com.timerx.ui.settings.background.BackgroundSettingsIntent.UpdateAlpha
import com.timerx.ui.settings.background.BackgroundSettingsIntent.UpdatePattern
import com.timerx.ui.settings.background.BackgroundSettingsState.LoadedState
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class BackgroundSettingsContainer(
    configurationFactory: ConfigurationFactory,
    backgroundSettingsManager: BackgroundSettingsManager
) : Container<BackgroundSettingsState, BackgroundSettingsIntent, Nothing> {
    override val store: Store<BackgroundSettingsState, BackgroundSettingsIntent, Nothing> =
        store(BackgroundSettingsState.Loading) {
            configure(configurationFactory, "Settings:Background")
            whileSubscribed {
                backgroundSettingsManager.backgroundSettings.collect {
                    updateState {
                        with(it) {
                            LoadedState(
                                backgroundAlpha = backgroundAlpha,
                                pattern = pattern
                            )
                        }
                    }
                }
            }

            reduce {
                when (it) {
                    is UpdateAlpha -> backgroundSettingsManager.setBackgroundAlpha(it.backgroundAlpha)
                    is UpdatePattern -> backgroundSettingsManager.setPattern(it.pattern)
                }
            }
        }
}
