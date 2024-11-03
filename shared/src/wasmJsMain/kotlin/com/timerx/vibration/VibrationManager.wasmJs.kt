@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
import kotlinx.browser.window
import kotlinx.coroutines.delay

class WasmVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    txDispatchers: TxDispatchers
) : AbstractVibrationManager(alertSettingsManager, timerManager, txDispatchers) {
    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        val millis = vibration.toMillis()
        repeat(vibration.repeat) {
            window.navigator.vibrate(millis.toInt())
            delay(VIBRATION_DELAY + millis)
        }
    }
}
