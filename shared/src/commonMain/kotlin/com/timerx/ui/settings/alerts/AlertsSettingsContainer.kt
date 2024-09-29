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

class AlertsSettingsContainer(
    private val timerXSettings: ITimerXSettings,
    private val permissionsHandler: IPermissionsHandler,
) : Container<AlertsSettingsState, AlertsSettingsIntent, Nothing> {

    override val store =
        store(AlertsSettingsState()) {
            whileSubscribed {
                timerXSettings.alertSettingsManager.alertSettings.collect {
                    updateState<AlertsSettingsState, _> {
                        AlertsSettingsState(
                            volume = it.volume,
                            isVibrationEnabled = it.vibrationEnabled,
                            isNotificationsEnabled = isNotificationsEnabled()
                        )
                    }
                }
            }

            reduce { settingsIntent ->
                when (settingsIntent) {
                    is AlertsSettingsIntent.UpdateVolume ->
                        timerXSettings.alertSettingsManager.setVolume(settingsIntent.volume)

                    is AlertsSettingsIntent.UpdateVibration ->
                        timerXSettings.alertSettingsManager.setVibrationEnabled(settingsIntent.enabled)

                    is AlertsSettingsIntent.EnableNotifications -> {
                        permissionsHandler.requestPermission(Permission.Notification)
                        updateState {
                            copy(isNotificationsEnabled = isNotificationsEnabled())
                        }
                    }

                    is AlertsSettingsIntent.OpenAppSettings ->
                        permissionsHandler.openAppSettings()
                }
            }
        }

    private suspend fun isNotificationsEnabled() =
        permissionsHandler.getPermissionState(Permission.Notification) == PermissionState.Granted
}
