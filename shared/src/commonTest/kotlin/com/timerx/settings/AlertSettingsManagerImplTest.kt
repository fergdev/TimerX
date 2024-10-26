package com.timerx.settings

import app.cash.turbine.test
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.platform.PlatformCapabilities
import com.timerx.platform.platformCapabilitiesOf
import com.timerx.settings.VibrationSetting.CanVibrate
import com.timerx.sound.Volume
import com.timerx.util.awaitAndExpectNoMore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers

fun alertSettingsOf(
    volume: Volume = Volume.default,
    vibrationSetting: VibrationSetting = VibrationSetting.CannotVibrate,
    ignoreNotificationsPermissions: Boolean = false,
    ttsVoiceId: String? = null,
) = AlertSettings(
    volume = volume,
    vibrationSetting = vibrationSetting,
    ignoreNotificationsPermissions = ignoreNotificationsPermissions,
    ttsVoiceId = ttsVoiceId
)

@OptIn(ExperimentalSettingsApi::class)
class AlertSettingsManagerImplTest : FreeSpec({
    val settings = MapSettings()
    val alertSettingsFactory: (PlatformCapabilities) -> AlertSettingsManagerImpl =
        { platformCapabilities ->
            AlertSettingsManagerImpl(
                flowSettings = settings.makeObservable().toFlowSettings(Dispatchers.Unconfined),
                platformCapabilities = platformCapabilities,
            )
        }
    val defaultSettingsFactory = { alertSettingsFactory(platformCapabilitiesOf()) }
    afterTest { settings.clear() }

    "default" {
        defaultSettingsFactory().alertSettings.test {
            awaitAndExpectNoMore() shouldBe alertSettingsOf()
        }
    }
    "set volume" {
        val alertSettings = defaultSettingsFactory()
        alertSettings.setVolume(Volume(0.5f))
        alertSettings.alertSettings.test {
            awaitAndExpectNoMore() shouldBe alertSettingsOf(volume = Volume(0.5f))
        }
    }

    "vibration" - {
        "default can vibrate" {
            val alertSettings = alertSettingsFactory(platformCapabilitiesOf(canVibrate = true))
            alertSettings.alertSettings.test {
                awaitAndExpectNoMore() shouldBe alertSettingsOf(
                    vibrationSetting = CanVibrate(true)
                )
            }
        }
        "throws when can't vibrate" {
            val alertSettings = defaultSettingsFactory()
            shouldThrow<IllegalStateException> {
                alertSettings.setVibrationEnabled(true)
            }.apply { message shouldBe "Cannot enable vibration when platform does not support it" }
        }
        "updates to true when can vibrate" {
            val alertSettings = alertSettingsFactory(platformCapabilitiesOf(canVibrate = true))
            alertSettings.setVibrationEnabled(true)
            alertSettings.alertSettings.test {
                awaitAndExpectNoMore() shouldBe alertSettingsOf(
                    vibrationSetting = CanVibrate(
                        true
                    )
                )
            }
        }
        "updates to false when can vibrate" {
            val alertSettings = alertSettingsFactory(platformCapabilitiesOf(canVibrate = true))
            alertSettings.setVibrationEnabled(false)
            alertSettings.alertSettings.test {
                awaitAndExpectNoMore() shouldBe alertSettingsOf(
                    vibrationSetting = CanVibrate(
                        false
                    )
                )
            }
        }
    }
    "ignore notifications permissions" {
        val alertSettings = defaultSettingsFactory()
        alertSettings.setIgnoreNotificationPermissions()
        alertSettings.alertSettings.test {
            awaitAndExpectNoMore() shouldBe alertSettingsOf(
                ignoreNotificationsPermissions = true
            )
        }
    }
    "set tts voice" {
        val alertSettings = defaultSettingsFactory()
        alertSettings.setTTSVoice("test")
        alertSettings.alertSettings.test {
            awaitAndExpectNoMore() shouldBe alertSettingsOf(ttsVoiceId = "test")
        }
    }
})
