package com.timerx.ui.settings.alerts

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.kompare.kompare
import com.timerx.settings.VibrationSetting.CanVibrate
import com.timerx.testutil.setContentWithLocals
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.kotest.core.spec.style.FreeSpec
import pro.respawn.flowmvi.api.DelicateStoreApi

@OptIn(ExperimentalTestApi::class, DelicateStoreApi::class)
class AlertSettingsContentTest : FreeSpec({
    val factory = { alertSettingsState: AlertsSettingsState ->
        mock<AlertSettingsComponent> {
            every { state } returns alertSettingsState
            every { onBackClicked } returns {}
        }
    }

    "state" - {
        "default" {
            runComposeUiTest {
                setContentWithLocals { AlertsSettingsContent(factory(AlertsSettingsState())) }
                kompare()
            }
        }
        "can vibrate but disabled" {
            runComposeUiTest {
                setContentWithLocals {
                    AlertsSettingsContent(
                        factory(
                            AlertsSettingsState(
                                vibration = CanVibrate()
                            )
                        )
                    )
                }
                kompare()
            }
        }
        "can vibrate and enabled" {
            runComposeUiTest {
                setContentWithLocals {
                    AlertsSettingsContent(
                        factory(
                            AlertsSettingsState(
                                vibration = CanVibrate(enabled = true)
                            )
                        )
                    )
                }
                kompare()
            }
        }
        "shows notifications enabled" {
            runComposeUiTest {
                setContentWithLocals {
                    AlertsSettingsContent(
                        factory(
                            AlertsSettingsState(areNotificationsEnabled = true)
                        )
                    )
                }
                kompare()
            }
        }
        "App OS settings displayed when available" {
            runComposeUiTest {
                setContentWithLocals {
                    AlertsSettingsContent(
                        factory(
                            AlertsSettingsState(canOpenOsSettings = true)
                        )
                    )
                }
                kompare()
            }
        }
    }
})
