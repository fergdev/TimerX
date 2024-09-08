package com.timerx.ui.settings

import kotlinx.serialization.Serializable
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@Serializable
data class SettingsState(
    val volume: Float = 1.0F,
    val vibration: Boolean = false,
    val notificationsEnabled: Boolean = false,
) : MVIState

interface SettingsIntent : MVIIntent {
    data class UpdateVolume(val volume: Float) : SettingsIntent
    data class UpdateVibration(val enabled: Boolean) : SettingsIntent
    data object EnableNotifications : SettingsIntent
    data object OpenAppSettings : SettingsIntent
}
