@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.coroutines.TDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager

class NoopVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    tDispatchers: TDispatchers
) : VibrationManager(alertSettingsManager, timerManager, tDispatchers) {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}
