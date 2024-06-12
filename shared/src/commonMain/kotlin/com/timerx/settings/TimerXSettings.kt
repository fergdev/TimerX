package com.timerx.settings

private const val VOLUME = "volume"

class TimerXSettings(private val settings: ISettingsManager) {

    var volume: Float
        get() = settings.getFloat(VOLUME, 1F)
        set(value) = settings.putFloat(VOLUME, value)
}