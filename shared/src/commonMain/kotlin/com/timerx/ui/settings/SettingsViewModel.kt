package com.timerx.ui.settings

import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.TimerXSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class SettingsScreenState(
    val volume: Float = 1.0F,
    val vibration: Boolean = false,
    val notificationsEnabled: Boolean = false,
)

class SettingsInteractions(
    val updateVolume: (Float) -> Unit,
    val updateVibration: (Boolean) -> Unit,
    val enableNotifications: () -> Unit,
    val openAppSettings: () -> Unit,
)

class SettingsViewModel(
    private val timerXSettings: TimerXSettings,
    private val permissionsHandler: IPermissionsHandler,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state

    val interactions = SettingsInteractions(
        updateVolume = ::updateVolume,
        updateVibration = ::updateVibration,
        enableNotifications = ::enableNotifications,
        openAppSettings = permissionsHandler::openAppSettings,
    )

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            timerXSettings.settings.collect { settings ->
                _state.update {
                    it.copy(
                        volume = settings.volume,
                        vibration = settings.vibrationEnabled,
                        notificationsEnabled = isNotificationsEnabled(),
                    )
                }
            }
        }
    }

    private suspend fun isNotificationsEnabled() =
        (permissionsHandler.getPermissionState(Permission.Notification)
                == PermissionState.Granted)

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

    private fun enableNotifications() {
        viewModelScope.launch {
            permissionsHandler.requestPermission(Permission.Notification)
            _state.update {
                it.copy(notificationsEnabled = isNotificationsEnabled())
            }
        }
    }
}