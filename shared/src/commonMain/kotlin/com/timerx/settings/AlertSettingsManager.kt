package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.timerx.util.mapIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val ALERT_SETTINGS = "alertSettings_"
private const val VIBRATION_ENABLED = "${ALERT_SETTINGS}vibrationEnabled"
private const val VOLUME = "${ALERT_SETTINGS}volume"
private const val SET_IGNORE_NOTIFICATIONS_PERMISSION =
    "${ALERT_SETTINGS}setIgnoreNotificationsPermission"
private const val TTS_VOICE_NAME = "${ALERT_SETTINGS}ttsVoiceName"

interface AlertSettingsManager {
    val alertSettings: Flow<AlertSettings>
    suspend fun setVolume(volume: Float)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setIgnoreNotificationPermissions()
    suspend fun setTTSVoice(voiceName: String)
}

@OptIn(ExperimentalSettingsApi::class)
class AlertSettingsManagerImpl(private val flowSettings: FlowSettings) : AlertSettingsManager {
    private val volume = flowSettings.getFloatOrNullFlow(VOLUME).mapIfNull(1F)
    private val vibrationEnabled =
        flowSettings.getBooleanOrNullFlow(VIBRATION_ENABLED).mapIfNull(true)
    private val ignoreNotificationsPermissions =
        flowSettings.getBooleanOrNullFlow(SET_IGNORE_NOTIFICATIONS_PERMISSION).mapIfNull(false)

    private val ttsVoiceName: Flow<String?> = flowSettings.getStringOrNullFlow(TTS_VOICE_NAME)

    override val alertSettings: Flow<AlertSettings> = combine(
        volume, vibrationEnabled, ignoreNotificationsPermissions, ttsVoiceName
    ) { volume, vibrationEnabled, ignoreNotificationPermissions, ttsVoiceName ->
        AlertSettings(
            volume = volume,
            vibrationEnabled = vibrationEnabled,
            ignoreNotificationsPermissions = ignoreNotificationPermissions,
            ttsVoiceName = ttsVoiceName
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

    override suspend fun setTTSVoice(voiceName: String) {
        flowSettings.putString(TTS_VOICE_NAME, voiceName)
    }
}
