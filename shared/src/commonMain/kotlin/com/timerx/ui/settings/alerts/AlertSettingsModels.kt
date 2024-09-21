package com.timerx.ui.settings.alerts

import kotlinx.serialization.Serializable
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@Serializable
data class AlertsSettingsState(
    val volume: Float = 1.0F,
    val vibration: Boolean = false,
    val notificationsEnabled: Boolean = false,
) : MVIState

interface AlertsSettingsIntent : MVIIntent {
    data class UpdateVolume(val volume: Float) : AlertsSettingsIntent
    data class UpdateVibration(val enabled: Boolean) : AlertsSettingsIntent
    data object EnableNotifications : AlertsSettingsIntent
    data object OpenAppSettings : AlertsSettingsIntent
}
