package com.timerx.ui.settings.alerts

import app.cash.turbine.test
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission.Notification
import com.timerx.permissions.PermissionState
import com.timerx.permissions.PermissionState.Granted
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.VibrationSetting
import com.timerx.settings.VibrationSetting.CanVibrate
import com.timerx.settings.VibrationSetting.CannotVibrate
import com.timerx.settings.alertSettingsOf
import com.timerx.sound.SoundManager
import com.timerx.sound.VoiceInformation
import com.timerx.sound.Volume
import com.timerx.testutil.asUnconfined
import com.timerx.testutil.idle
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.flowOf
import pro.respawn.flowmvi.test.subscribeAndTest

private val voiceInformation1 = VoiceInformation("testId1", "testName1")
private val voiceInformation2 = VoiceInformation("testId2", "testName2")
private val voiceInformation3 = VoiceInformation("testId3", "testName3")
private val voices = listOf(voiceInformation1, voiceInformation2, voiceInformation3)

private fun alertSettingsStateOf(
    volume: Volume = Volume.default,
    vibration: VibrationSetting = CannotVibrate,
    isNotificationsEnabled: Boolean = false,
    selectedVoice: VoiceInformation = VoiceInformation.DeviceDefault,
    availableVoices: ImmutableSet<VoiceInformation> = voices.toPersistentSet()
) = AlertsSettingsState(
    volume = volume,
    vibration = vibration,
    isNotificationsEnabled = isNotificationsEnabled,
    selectedVoice = selectedVoice,
    availableVoices = availableVoices
)

class AlertSettingsContainerTest : FreeSpec({
    asUnconfined()
    val alertSettingsManager = mock<AlertSettingsManager>()
    val permissionsHandler = mock<IPermissionsHandler>()
    val soundManager = mock<SoundManager>()
    val container = {
        AlertsSettingsContainer(
            alertSettingsManager = alertSettingsManager,
            permissionsHandler = permissionsHandler,
            soundManager = soundManager,
        )
    }

    beforeEach {
        everySuspend {
            permissionsHandler.getPermissionState(Notification)
        } returns PermissionState.Denied
        every { soundManager.voices() } returns voices
        every { alertSettingsManager.alertSettings } returns flowOf(alertSettingsOf())
    }

    afterEach {
        resetAnswers(alertSettingsManager, permissionsHandler, soundManager)
    }

    "initial state" {
        AlertsSettingsContainer(
            alertSettingsManager,
            permissionsHandler,
            soundManager,
        ).store.subscribeAndTest {
            states.test {
                awaitItem() shouldBe AlertsSettingsState()
            }
        }
    }
    "sets state from settings" - {
        "default" {
            container().store.subscribeAndTest {
                states.test {
                    awaitItem()
                    awaitItem() shouldBe alertSettingsStateOf()
                    expectNoEvents()
                }
            }
        }
        "set volume" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                alertSettingsOf(volume = Volume(0.1f))
            )

            container().store.subscribeAndTest {
                states.test {
                    awaitItem()
                    awaitItem() shouldBe alertSettingsStateOf(volume = Volume(0.1f))
                    expectNoEvents()
                }
            }
        }
        "sets vibration" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                alertSettingsOf(
                    vibrationSetting = CanVibrate(true)
                )
            )

            container().store.subscribeAndTest {
                states.test {
                    awaitItem()
                    awaitItem() shouldBe alertSettingsStateOf(vibration = CanVibrate(true))
                    expectNoEvents()
                }
            }
        }
        "sets selected voice" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                alertSettingsOf(ttsVoiceId = voiceInformation1.id)
            )

            AlertsSettingsContainer(
                alertSettingsManager = alertSettingsManager,
                permissionsHandler = permissionsHandler,
                soundManager = soundManager,
            ).store.subscribeAndTest {
                states.test {
                    awaitItem()
                    awaitItem() shouldBe alertSettingsStateOf(selectedVoice = voiceInformation1)
                    expectNoEvents()
                }
            }
        }
        "set is notifications enabled" {
            everySuspend {
                permissionsHandler.getPermissionState(Notification)
            } returns Granted

            container().store.subscribeAndTest {
                states.test {
                    awaitItem()
                    awaitItem() shouldBe alertSettingsStateOf(isNotificationsEnabled = true)
                    expectNoEvents()
                }
            }
        }
    }
    "intent" - {
        "update volume invokes settings" {
            mock<AlertSettingsManager> {
                everySuspend { alertSettingsManager.setVolume(any()) } returns Unit
            }
            container().store.subscribeAndTest {
                intent(AlertsSettingsIntent.UpdateVolume(Volume(0.1f)))
                idle()
                verifySuspend {
                    alertSettingsManager.setVolume(Volume(0.1f))
                }
            }
        }
        "update vibration invokes settings" {
            mock<AlertSettingsManager> {
                everySuspend { alertSettingsManager.setVibrationEnabled(true) } returns Unit
            }
            container().store.subscribeAndTest {
                intent(AlertsSettingsIntent.UpdateVibration(true))
                idle()
                verifySuspend { alertSettingsManager.setVibrationEnabled(true) }
            }
        }
        "enable notifications invokes permissions and sets state" {
            mock<IPermissionsHandler> {
                everySuspend { permissionsHandler.requestPermission(Notification) } returns Unit
            }
            container().store.subscribeAndTest {
                states.test {
                    awaitItem()
                }
                mock<IPermissionsHandler> {
                    everySuspend {
                        permissionsHandler.getPermissionState(Notification)
                    } returns Granted
                }
                intent(AlertsSettingsIntent.EnableNotifications)
                idle()
                states.test {
                    awaitItem().isNotificationsEnabled shouldBe true
                    expectNoEvents()
                }
                verifySuspend { permissionsHandler.requestPermission(Notification) }
            }
        }
        "open app settings invokes permission handler" {
            mock<IPermissionsHandler> {
                everySuspend { permissionsHandler.openAppSettings() } returns Unit
            }
            container().store.subscribeAndTest {
                intent(AlertsSettingsIntent.OpenAppSettings)
                idle()
                verifySuspend { permissionsHandler.openAppSettings() }
            }
        }
        "setTTS voice" {
            mock<AlertSettingsManager> {
                everySuspend { alertSettingsManager.setTTSVoice(any()) } returns Unit
            }
            mock<SoundManager> {
                everySuspend { soundManager.textToSpeech(DEMO_TTS_TEXT) } returns Unit
            }
            container().store.subscribeAndTest {
                intent(AlertsSettingsIntent.SetTTSVoice(voiceInformation2))
                idle()
                verifySuspend { alertSettingsManager.setTTSVoice(voiceInformation2.id) }
                verifySuspend { soundManager.textToSpeech(DEMO_TTS_TEXT) }
            }
        }
    }
})
