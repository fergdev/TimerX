package com.timerx.ui.settings.background

import com.timerx.settings.BackgroundSettingsManager
import com.timerx.settings.TimerXSettings
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class BackgroundSettingsContainer(
    timerXSettings: TimerXSettings,
) : Container<BackgroundSettingsState, BackgroundSettingsIntent, Nothing> {
    override val store: Store<BackgroundSettingsState, BackgroundSettingsIntent, Nothing> =
        store(BackgroundSettingsState.Loading) {
            val backgroundSettingsManager: BackgroundSettingsManager =
                timerXSettings.backgroundSettingsManager
            whileSubscribed {
                backgroundSettingsManager.backgroundSettings.collect {
                    updateState {
                        with(it) {
                            BackgroundSettingsState.LoadedState(
                                alpha = alpha,
                                pattern = pattern
                            )
                        }
                    }
                }
            }

            reduce {
                when (it) {
                    is BackgroundSettingsIntent.UpdateAlpha ->
                        backgroundSettingsManager.setAlpha(it.alpha)

                    is BackgroundSettingsIntent.UpdatePattern ->
                        backgroundSettingsManager.setPattern(it.pattern)
                }
            }
        }
}
