package com.timerx.ui.settings

import com.timerx.settings.TimerXSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

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
            volume = 1F,
            vibration = true,
        )
    )
    val state: StateFlow<SettingsScreenState> = _state
    val interactions = SettingsInteractions(
        updateVolume = ::updateVolume,
        updateVibration = ::updateVibration
    )

    init {
        viewModelScope.launch {
            timerXSettings.settings.collect { settings ->
                _state.update {
                    it.copy(
                        volume = settings.volume,
                        vibration = settings.vibrationEnabled
                    )
                }
            }
        }
    }

    private fun updateVibration(enabled: Boolean) {
        viewModelScope.launch {
            timerXSettings.setVibrationEnabled(enabled)
        }
    }

    private fun updateVolume(volume: Float) {
        viewModelScope.launch {
            timerXSettings.setVolume(volume)
        }
    }
}