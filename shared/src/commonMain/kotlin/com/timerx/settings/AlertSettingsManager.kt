package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.timerx.util.mapIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val VIBRATION_ENABLED = "vibrationEnabled"
private const val VOLUME = "volume"
private const val SET_IGNORE_NOTIFICATIONS_PERMISSION = "setIgnoreNotificationsPermission"

interface AlertSettingsManager {
    val alertSettings: Flow<AlertSettings>
    suspend fun setVolume(volume: Float)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setIgnoreNotificationPermissions()
}

@OptIn(ExperimentalSettingsApi::class)
class AlertSettingsManagerImpl(private val flowSettings: FlowSettings) : AlertSettingsManager {
    private val volume = flowSettings.getFloatOrNullFlow(VOLUME).mapIfNull(1F)
    private val vibrationEnabled =
        flowSettings.getBooleanOrNullFlow(VIBRATION_ENABLED).mapIfNull(true)
    private val ignoreNotificationsPermissions =
        flowSettings.getBooleanOrNullFlow(SET_IGNORE_NOTIFICATIONS_PERMISSION).mapIfNull(false)

    override val alertSettings: Flow<AlertSettings> = combine(
        volume, vibrationEnabled, ignoreNotificationsPermissions
    ) { a, b, c ->
        AlertSettings(
            volume = a,
            vibrationEnabled = b,
            ignoreNotificationsPermissions = c,
        )
    }

    override suspend fun setVolume(volume: Float) {
        flowSettings.putFloat(VOLUME, volume)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        flowSettings.putBoolean(VIBRATION_ENABLED, enabled)
    }

    override suspend fun setIgnoreNotificationPermissions() {
        flowSettings.putBoolean(SET_IGNORE_NOTIFICATIONS_PERMISSION, true)
    }

}

