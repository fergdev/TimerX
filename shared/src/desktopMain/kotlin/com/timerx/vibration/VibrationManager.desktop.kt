@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager

class NoopVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager
) : VibrationManager(alertSettingsManager, timerManager) {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}
