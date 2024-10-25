package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.timerx.platform.PlatformCapabilities
import com.timerx.sound.Volume
import com.timerx.util.assert
import com.timerx.util.mapIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val ALERT_SETTINGS = "alertSettings_"
private const val VIBRATION_ENABLED = "${ALERT_SETTINGS}vibrationEnabled"
private const val VOLUME = "${ALERT_SETTINGS}volume"
private const val SET_IGNORE_NOTIFICATIONS_PERMISSION =
    "${ALERT_SETTINGS}setIgnoreNotificationsPermission"
private const val TTS_VOICE_NAME = "${ALERT_SETTINGS}ttsVoiceName"

data class AlertSettings(
    val volume: Volume,
    val vibrationState: VibrationState,
    val ignoreNotificationsPermissions: Boolean,
    val ttsVoiceId: String?,
)

interface AlertSettingsManager {
    val alertSettings: Flow<AlertSettings>
    suspend fun setVolume(volume: Volume)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setIgnoreNotificationPermissions()
    suspend fun setTTSVoice(voiceName: String)
}

sealed interface VibrationState {
    data class CanVibrate(val enabled: Boolean = false) : VibrationState
    data object CannotVibrate : VibrationState
}

@OptIn(ExperimentalSettingsApi::class)
class AlertSettingsManagerImpl(
    private val flowSettings: FlowSettings,
    private val platformCapabilities: PlatformCapabilities
) : AlertSettingsManager {
    private val volume = flowSettings.getFloatOrNullFlow(VOLUME)
        .map { if (it == null) Volume.default else Volume(it) }
    private val vibrationState =
        flowSettings.getBooleanOrNullFlow(VIBRATION_ENABLED)
            .map {
                if (!platformCapabilities.canVibrate) VibrationState.CannotVibrate
                VibrationState.CanVibrate(it != null && it)
            }
    private val ignoreNotificationsPermissions =
        flowSettings.getBooleanOrNullFlow(SET_IGNORE_NOTIFICATIONS_PERMISSION).mapIfNull(false)

    private val ttsVoiceId: Flow<String?> = flowSettings.getStringOrNullFlow(TTS_VOICE_NAME)

    override val alertSettings: Flow<AlertSettings> = combine(
        volume, vibrationState, ignoreNotificationsPermissions, ttsVoiceId
    ) { volume, vibrationState, ignoreNotificationPermissions, ttsVoiceName ->
        AlertSettings(
            volume = volume,
            vibrationState = vibrationState,
            ignoreNotificationsPermissions = ignoreNotificationPermissions,
            ttsVoiceId = ttsVoiceName
        )
    }

    override suspend fun setVolume(volume: Volume) {
        flowSettings.putFloat(VOLUME, volume.value)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        assert(platformCapabilities.canVibrate) {
            "Cannot enable vibration when platform does not support it"
        }
        flowSettings.putBoolean(VIBRATION_ENABLED, enabled)
    }

    override suspend fun setIgnoreNotificationPermissions() {
        flowSettings.putBoolean(SET_IGNORE_NOTIFICATIONS_PERMISSION, true)
    }

    override suspend fun setTTSVoice(voiceName: String) {
        flowSettings.putString(TTS_VOICE_NAME, voiceName)
    }
}
