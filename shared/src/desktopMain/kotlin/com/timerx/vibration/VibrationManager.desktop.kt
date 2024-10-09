@file:Suppress("Filename")

package com.timerx.vibration

class NoopVibrationManager : IVibrationManager {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}
