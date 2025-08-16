@file:Suppress("Filename")

package com.intervallum.vibration

import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.timermanager.TimerManager

class NoopVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    txDispatchers: TxDispatchers
) : AbstractVibrationManager(alertSettingsManager, timerManager, txDispatchers) {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}
