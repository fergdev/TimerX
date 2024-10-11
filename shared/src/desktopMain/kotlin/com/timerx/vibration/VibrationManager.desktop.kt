@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.settings.ITimerXSettings
import com.timerx.timermanager.TimerManager

class NoopVibrationManager(
    timerXSettings: ITimerXSettings, timerManager: TimerManager
) : VibrationManager(timerXSettings, timerManager) {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}
