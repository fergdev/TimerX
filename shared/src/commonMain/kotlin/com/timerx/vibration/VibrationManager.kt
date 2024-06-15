package com.timerx.vibration

import com.timerx.settings.TimerXSettings

interface VibrationManager {
    fun vibrate(vibration: Vibration)
}

expect fun getVibrationManager(timerXSettings: TimerXSettings): VibrationManager

enum class Vibration(val displayName: String) {
    Soft("Soft"),
    Light("Light"),
    Medium("Medium"),
    Rigid("Rigid"),
    Heavy("Heavy"),
    None("None")
}