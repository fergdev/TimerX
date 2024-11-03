@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
import kotlinx.browser.window

class WasmVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    txDispatchers: TxDispatchers
) : VibrationManager(alertSettingsManager, timerManager, txDispatchers) {
    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        window.navigator.vibrate(1000)
    }
}
