@file:Suppress("Filename")

package com.intervallum.vibration

import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.timermanager.TimerManager
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
