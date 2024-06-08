package com.timerx.beep

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VolumeManager {
    private val _volumeFlow = MutableStateFlow(1F)
    val volumeFlow: StateFlow<Float>
        get() = _volumeFlow

    fun setVolume(volume: Float) {
        _volumeFlow.value = volume.coerceIn(0F, 1F)
    }

    fun getVolume(): Float {
        return _volumeFlow.value
    }
}
