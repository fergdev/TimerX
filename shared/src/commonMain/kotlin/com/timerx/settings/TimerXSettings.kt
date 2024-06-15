package com.timerx.settings

private const val VOLUME = "volume"
private const val VIBRATION_ENABLE = "vibration_enabled"

class TimerXSettings(private val settings: ISettingsManager) {

    var volume: Float
        get() = settings.getFloat(VOLUME, 1F)
        set(value) = settings.putFloat(VOLUME, value)

    var vibrationEnabled: Boolean
        get() = settings.getBoolean(VIBRATION_ENABLE, true)
        set(value) = settings.putBoolean(VIBRATION_ENABLE, value)

}