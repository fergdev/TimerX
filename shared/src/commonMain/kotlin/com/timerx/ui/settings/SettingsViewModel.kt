package com.timerx.ui.settings

import com.timerx.settings.TimerXSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel

data class SettingsScreenState(
    val volume: Float = 1.0F,
    val vibration: Boolean
)

class SettingsInteractions(
    val updateVolume: (Float) -> Unit,
    val updateVibration: (Boolean) -> Unit
)

class SettingsViewModel(
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsScreenState(
            volume = timerXSettings.volume,
            vibration = timerXSettings.vibrationEnabled
        )
    )
    val state: StateFlow<SettingsScreenState> = _state
    val interactions = SettingsInteractions(
        updateVolume = ::updateVolume,
        updateVibration = ::updateVibration
    )

    private fun updateVibration(enabled: Boolean){
        timerXSettings.vibrationEnabled = enabled
        _state.update { it.copy(vibration = enabled) }

    }

    private fun updateVolume(volume: Float) {
        timerXSettings.volume = volume
        _state.update { it.copy(volume = volume) }
    }
}