package com.timerx.settings

private const val VOLUME = "volume"

class TimerXSettings(private val settings: SettingsManager) {

    var volume: Float
        get() = settings.getFloat(VOLUME)
        set(value) = settings.putFloat(VOLUME, value)
}