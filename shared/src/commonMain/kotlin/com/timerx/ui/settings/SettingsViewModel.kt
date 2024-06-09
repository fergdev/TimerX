package com.timerx.ui.settings

import com.timerx.settings.TimerXSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel

data class SettingsScreenState(
    val volume: Float = 1.0F
)

class SettingsInteractions(
    val updateVolume: (Float) -> Unit
)

class SettingsViewModel(
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsScreenState(
            volume = timerXSettings.volume
        )
    )
    val state: StateFlow<SettingsScreenState> = _state
    val interactions = SettingsInteractions(
        updateVolume = ::updateVolume
    )

    private fun updateVolume(volume: Float) {
        timerXSettings.volume = volume
        _state.update { it.copy(volume = volume) }
    }
}