package com.intervallum.ui.settings.alerts

import com.intervallum.permissions.IPermissionsHandler
import com.intervallum.permissions.Permission
import com.intervallum.permissions.PermissionState
import com.intervallum.platform.PlatformCapabilities
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.sound.SoundManager
import com.intervallum.sound.VoiceInformation
import com.intervallum.ui.di.ConfigurationFactory
import com.intervallum.ui.di.configure
import com.intervallum.ui.settings.alerts.AlertsSettingsIntent.EnableNotifications
import com.intervallum.ui.settings.alerts.AlertsSettingsIntent.OpenAppSettings
import com.intervallum.ui.settings.alerts.AlertsSettingsIntent.SetTTSVoice
import com.intervallum.ui.settings.alerts.AlertsSettingsIntent.UpdateVibration
import com.intervallum.ui.settings.alerts.AlertsSettingsIntent.UpdateVolume
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.delay
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

const val DEMO_TTS_TEXT = "Work. Rest"

class AlertsSettingsContainer(
    configurationFactory: ConfigurationFactory,
    private val alertSettingsManager: AlertSettingsManager,
    private val permissionsHandler: IPermissionsHandler,
    private val soundManager: SoundManager,
    private val platformCapabilities: PlatformCapabilities
) : Container<AlertsSettingsState, AlertsSettingsIntent, Nothing> {

    override val store =
        store(AlertsSettingsState()) {
            configure(configurationFactory, "Settings:Alert")
            whileSubscribed {
                alertSettingsManager.alertSettings.collect {
                    updateState<AlertsSettingsState, _> {
                        val voices = soundManager.voices()
                        AlertsSettingsState(
                            volume = it.volume,
                            vibration = it.vibrationSetting,
                            areNotificationsEnabled = isNotificationsEnabled(),
                            canOpenOsSettings = platformCapabilities.canOpenOsSettings,
                            selectedVoice = voices.selected(it.ttsVoiceId),
                            availableVoices = voices.sortedBy { it.name }.toPersistentSet()
                        )
                    }
                }
            }

            reduce { settingsIntent ->
                when (settingsIntent) {
                    is UpdateVolume ->
                        alertSettingsManager.setVolume(settingsIntent.volume)

                    is UpdateVibration ->
                        alertSettingsManager.setVibrationEnabled(settingsIntent.enabled)

                    is EnableNotifications -> {
                        permissionsHandler.requestPermission(Permission.Notification)
                        updateState { copy(areNotificationsEnabled = isNotificationsEnabled()) }
                    }

                    is OpenAppSettings ->
                        permissionsHandler.openAppSettings()

                    is SetTTSVoice -> {
                        alertSettingsManager.setTTSVoice(settingsIntent.voiceInformation.id)
                        // Work around for collectors not receiving data before put returns
                        // https://github.com/xxfast/KStore/issues/129
                        delay(100)
                        soundManager.textToSpeech(DEMO_TTS_TEXT)
                    }
                }
            }
        }

    private fun List<VoiceInformation>.selected(voiceId: String?) =
        this.firstOrNull { it.id == voiceId } ?: VoiceInformation.DeviceDefault

    private suspend fun isNotificationsEnabled() =
        permissionsHandler.getPermissionState(Permission.Notification) == PermissionState.Granted
}
