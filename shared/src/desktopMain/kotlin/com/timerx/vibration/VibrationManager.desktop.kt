@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerManager

class NoopVibrationManager(
    timerXSettings: TimerXSettings,
    timerManager: TimerManager
) : VibrationManager(timerXSettings, timerManager) {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}
