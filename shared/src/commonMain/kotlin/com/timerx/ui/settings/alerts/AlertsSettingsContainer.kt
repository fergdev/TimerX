package com.timerx.ui.settings.alerts

import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.ITimerXSettings
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

internal class AlertsSettingsContainer(
    private val timerXSettings: ITimerXSettings,
    private val permissionsHandler: IPermissionsHandler,
) : Container<AlertsSettingsState, AlertsSettingsIntent, Nothing> {

    override val store =
        store(AlertsSettingsState()) {
            whileSubscribed {
                timerXSettings.alertSettings.collect {
                    updateState<AlertsSettingsState, _> {
                        AlertsSettingsState(
                            volume = it.volume,
                            vibration = it.vibrationEnabled,
                            notificationsEnabled = isNotificationsEnabled()
                        )
                    }
                }
            }

            reduce { settingsIntent ->
                when (settingsIntent) {
                    is AlertsSettingsIntent.UpdateVolume -> {
                        timerXSettings.setVolume(settingsIntent.volume)
                    }

                    is AlertsSettingsIntent.UpdateVibration -> {
                        timerXSettings.setVibrationEnabled(settingsIntent.enabled)
                    }

                    is AlertsSettingsIntent.EnableNotifications -> {
                        permissionsHandler.requestPermission(Permission.Notification)
                        updateState {
                            copy(notificationsEnabled = isNotificationsEnabled())
                        }
                    }

                    is AlertsSettingsIntent.OpenAppSettings -> {
                        permissionsHandler.openAppSettings()
                    }
                }
            }
        }

    private suspend fun isNotificationsEnabled() =
        permissionsHandler.getPermissionState(Permission.Notification) == PermissionState.Granted
}
