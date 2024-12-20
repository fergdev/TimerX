package com.timerx.ui.settings.alerts

import com.timerx.settings.VibrationSetting
import com.timerx.sound.VoiceInformation
import com.timerx.sound.Volume
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

data class AlertsSettingsState(
    val volume: Volume = Volume.default,
    val vibration: VibrationSetting = VibrationSetting.CannotVibrate,
    val areNotificationsEnabled: Boolean = false,
    val canOpenOsSettings: Boolean = false,
    val selectedVoice: VoiceInformation = VoiceInformation.DeviceDefault,
    val availableVoices: ImmutableSet<VoiceInformation> = persistentSetOf()
) : MVIState

interface AlertsSettingsIntent : MVIIntent {
    data class UpdateVolume(val volume: Volume) : AlertsSettingsIntent
    data class UpdateVibration(val enabled: Boolean) : AlertsSettingsIntent
    data object EnableNotifications : AlertsSettingsIntent
    data object OpenAppSettings : AlertsSettingsIntent
    data class SetTTSVoice(val voiceInformation: VoiceInformation) : AlertsSettingsIntent
}
