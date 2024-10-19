package com.timerx.ui.settings.alerts

import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.platform.PlatformCapabilities
import com.timerx.settings.TimerXSettings
import com.timerx.sound.SoundManager
import com.timerx.sound.VoiceInformation
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.EnableNotifications
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.OpenAppSettings
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.SetTTSVoice
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.UpdateVibration
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.UpdateVolume
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.delay
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class AlertsSettingsContainer(
    private val settings: TimerXSettings,
    private val permissionsHandler: IPermissionsHandler,
    private val platformCapabilities: PlatformCapabilities,
    private val soundManager: SoundManager
) : Container<AlertsSettingsState, AlertsSettingsIntent, Nothing> {

    override val store =
        store(AlertsSettingsState()) {
            whileSubscribed {
                settings.alertSettingsManager.alertSettings.collect {
                    updateState<AlertsSettingsState, _> {
                        val voices = soundManager.voices()
                        AlertsSettingsState(
                            volume = it.volume,
                            isVibrationEnabled = it.vibrationEnabled,
                            canVibrate = platformCapabilities.canVibrate,
                            isNotificationsEnabled = isNotificationsEnabled(),
                            selectedVoice = voices.selected(it.ttsVoiceName),
                            availableVoices = voices.sortedBy { it.name }.toPersistentSet()
                        )
                    }
                }
            }

            reduce { settingsIntent ->
                when (settingsIntent) {
                    is UpdateVolume ->
                        settings.alertSettingsManager.setVolume(settingsIntent.volume)

                    is UpdateVibration ->
                        settings.alertSettingsManager.setVibrationEnabled(settingsIntent.enabled)

                    is EnableNotifications -> {
                        permissionsHandler.requestPermission(Permission.Notification)
                        updateState { copy(isNotificationsEnabled = isNotificationsEnabled()) }
                    }

                    is OpenAppSettings ->
                        permissionsHandler.openAppSettings()

                    is SetTTSVoice -> {
                        settings.alertSettingsManager.setTTSVoice(settingsIntent.voiceId)
                        // Work around for collectors not receiving data before put returns
                        // https://github.com/xxfast/KStore/issues/129
                        delay(100)
                        soundManager.textToSpeech("Work. Rest. Finished.")
                    }
                }
            }
        }

    private fun List<VoiceInformation>.selected(voiceId: String?) =
        this.firstOrNull { it.id == voiceId } ?: VoiceInformation.DeviceDefault

    private suspend fun isNotificationsEnabled() =
        permissionsHandler.getPermissionState(Permission.Notification) == PermissionState.Granted
}
