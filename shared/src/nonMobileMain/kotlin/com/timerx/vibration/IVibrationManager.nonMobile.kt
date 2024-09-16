package com.timerx.vibration

object VibrationManager : IVibrationManager {
    override suspend fun vibrate(vibration: Vibration) {
        println("BRRRRRRr")
    }
}