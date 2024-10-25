package com.timerx.settings

import com.timerx.sound.Volume

fun alertSettingsOf(
    volume: Volume = Volume.default,
    vibrationState: VibrationState = VibrationState.CannotVibrate,
    ignoreNotificationsPermissions: Boolean = false,
    ttsVoiceId: String? = null,
) = AlertSettings(
    volume = volume,
    vibrationState = vibrationState,
    ignoreNotificationsPermissions = ignoreNotificationsPermissions,
    ttsVoiceId = ttsVoiceId
)
