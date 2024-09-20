package com.timerx.vibration

object NoopVibrationManager : IVibrationManager {
    override suspend fun vibrate(vibration: Vibration) {
        // Noop
    }
}