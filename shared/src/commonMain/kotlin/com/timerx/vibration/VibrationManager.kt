package com.timerx.vibration

import com.timerx.settings.TimerXSettings

interface VibrationManager {
    fun vibrate(vibration: Vibration)
}

expect fun getVibrationManager(timerXSettings: TimerXSettings): VibrationManager

enum class Vibration(val displayName: String, val repeat: Int) {
    Soft("Soft", 1),
    SoftX2("Soft X2", 2),
    SoftX3("Soft X3", 3),
    Light("Light", 1),
    LightX2("Light", 2),
    LightX3("Light", 3),
    Medium("Medium", 1),
    MediumX2("Medium X2", 2),
    MediumX3("Medium X3", 3),
    Rigid("Rigid", 1),
    RigidX2("Rigid X2", 2),
    RigidX3("Rigid X3", 3),
    Heavy("Heavy", 1),
    HeavyX2("Heavy X2", 2),
    HeavyX3("Heavy X3", 3),
    None("None", 1)
}