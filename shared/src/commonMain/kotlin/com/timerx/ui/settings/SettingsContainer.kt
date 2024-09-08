package com.timerx.ui.settings

import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.TimerXSettings
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.logging.StoreLogLevel
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

internal class SettingsContainer(
    private val timerXSettings: TimerXSettings,
    private val permissionsHandler: IPermissionsHandler,
) : Container<SettingsState, SettingsIntent, Nothing> {

    override val store =
        store(SettingsState()) {
            enableLogging(tag = "####", level = StoreLogLevel.Trace)

            whileSubscribed {
                timerXSettings.settings.collect {
                    updateState<SettingsState, _> {
                        SettingsState(
                            volume = it.volume,
                            vibration = it.vibrationEnabled,
                            notificationsEnabled = isNotificationsEnabled()
                        )
                    }
                }
            }

            reduce { settingsIntent ->
                when (settingsIntent) {
                    is SettingsIntent.UpdateVolume -> {
                        timerXSettings.setVolume(settingsIntent.volume)
                    }

                    is SettingsIntent.UpdateVibration -> {
                        timerXSettings.setVibrationEnabled(settingsIntent.enabled)
                    }

                    is SettingsIntent.EnableNotifications -> {
                        permissionsHandler.requestPermission(Permission.Notification)
                        updateState {
                            copy(notificationsEnabled = isNotificationsEnabled())
                        }
                    }

                    is SettingsIntent.OpenAppSettings -> {
                        permissionsHandler.openAppSettings()
                    }
                }
            }
        }

    private suspend fun isNotificationsEnabled() =
        (permissionsHandler.getPermissionState(Permission.Notification)
                == PermissionState.Granted)
}
