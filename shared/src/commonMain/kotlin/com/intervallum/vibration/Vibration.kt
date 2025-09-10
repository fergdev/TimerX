package com.intervallum.vibration

import com.intervallum.vibration.Vibration.Heavy
import com.intervallum.vibration.Vibration.HeavyX2
import com.intervallum.vibration.Vibration.HeavyX3
import com.intervallum.vibration.Vibration.Light
import com.intervallum.vibration.Vibration.LightX2
import com.intervallum.vibration.Vibration.LightX3
import com.intervallum.vibration.Vibration.Medium
import com.intervallum.vibration.Vibration.MediumX2
import com.intervallum.vibration.Vibration.MediumX3
import com.intervallum.vibration.Vibration.None
import com.intervallum.vibration.Vibration.Rigid
import com.intervallum.vibration.Vibration.RigidX2
import com.intervallum.vibration.Vibration.RigidX3
import com.intervallum.vibration.Vibration.Soft
import com.intervallum.vibration.Vibration.SoftX2
import com.intervallum.vibration.Vibration.SoftX3

private const val HEAVY_MILLIS = 500L
private const val MEDIUM_MILLIS = 400L
private const val RIGID_MILLIS = 300L
private const val LIGHT_MILLIS = 200L
private const val SOFT_MILLS = 100L

fun Vibration.toMillis() = when (this) {
    Heavy, HeavyX2, HeavyX3 -> HEAVY_MILLIS
    Medium, MediumX2, MediumX3 -> MEDIUM_MILLIS
    Rigid, RigidX2, RigidX3 -> RIGID_MILLIS
    Light, LightX2, LightX3 -> LIGHT_MILLIS
    Soft, SoftX2, SoftX3 -> SOFT_MILLS
    None -> 0L
}

enum class Vibration(val displayName: String, val repeat: Int) {
    Soft("Soft", 1),
    SoftX2("Soft X2", 2),
    SoftX3("Soft X3", 3),
    Light("Light", 1),
    LightX2("Light X2", 2),
    LightX3("Light X3", 3),
    Medium("Medium", 1),
    MediumX2("Medium X2", 2),
    MediumX3("Medium X3", 3),
    Rigid("Rigid", 1),
    RigidX2("Rigid X2", 2),
    RigidX3("Rigid X3", 3),
    Heavy("Heavy", 1),
    HeavyX2("Heavy X2", 2),
    HeavyX3("Heavy X3", 3),
    None("None", 0)
}
