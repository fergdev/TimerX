package com.timerx.beep

import com.timerx.settings.TimerXSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VolumeManager(private val timerXSettings: TimerXSettings) {
    private val _volumeFlow = MutableStateFlow(timerXSettings.volume)
    val volumeFlow: StateFlow<Float>
        get() = _volumeFlow

    fun setVolume(volume: Float) {
        val coercedVolume = volume.coerceIn(0F, 1F)
        _volumeFlow.value = coercedVolume
        timerXSettings.volume = coercedVolume
    }

    fun getVolume(): Float {
        return _volumeFlow.value
    }
}
