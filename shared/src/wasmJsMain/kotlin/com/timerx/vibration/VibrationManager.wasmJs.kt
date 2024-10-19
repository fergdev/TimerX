@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerManager
import kotlinx.browser.window

class WasmVibrationManager(
    timerXSettings: TimerXSettings,
    timerManager: TimerManager
) : VibrationManager(timerXSettings, timerManager) {
    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        window.navigator.vibrate(1000)
    }
}
