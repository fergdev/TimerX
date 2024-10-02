package com.timerx.vibration

import kotlinx.browser.window

class VibrationManager : IVibrationManager {
    override suspend fun vibrate(vibration: Vibration) {
        window.navigator.vibrate(1000)
    }
}
