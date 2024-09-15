package com.timerx.vibration

import com.timerx.settings.ITimerXSettings

actual fun getVibrationManager(timerXSettings: ITimerXSettings)=object : IVibrationManager {
    override suspend fun vibrate(vibration: Vibration) {
        println("BRRRRRRr")
    }
}