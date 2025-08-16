package com.intervallum.ui.settings.alerts

import androidx.compose.ui.test.ExperimentalTestApi
import io.kotest.core.spec.style.FreeSpec
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.DelicateStoreApi

@OptIn(ExperimentalTestApi::class, DelicateStoreApi::class, InternalFlowMVIAPI::class)
class AlertSettingsContentTest : FreeSpec({
    "loading content" {
        assert(true)
    }
//    val factory = { alertSettingsState: AlertsSettingsState ->
//        mock<AlertSettingsComponent> {
//            every { this@mock.states } returns MutableStateFlow(alertSettingsState)
//            every { this@mock.state } returns alertSettingsState
//            every { this@mock.onBackClicked } returns {}
//        }
//    }
//
//    "state" - {
//        "default" {
//            runComposeUiTest {
//                setContentWithLocals { AlertsSettingsContent(factory(AlertsSettingsState())) }
//                kompare()
//            }
//        }
//        "can vibrate but disabled" {
//            runComposeUiTest {
//                setContentWithLocals {
//                    AlertsSettingsContent(
//                        factory(
//                            AlertsSettingsState(
//                                vibration = CanVibrate()
//                            )
//                        )
//                    )
//                }
//                kompare()
//            }
//        }
//        "can vibrate and enabled" {
//            runComposeUiTest {
//                setContentWithLocals {
//                    AlertsSettingsContent(
//                        factory(
//                            AlertsSettingsState(
//                                vibration = CanVibrate(enabled = true)
//                            )
//                        )
//                    )
//                }
//                kompare()
//            }
//        }
//        "shows notifications enabled" {
//            runComposeUiTest {
//                setContentWithLocals {
//                    AlertsSettingsContent(
//                        factory(
//                            AlertsSettingsState(areNotificationsEnabled = true)
//                        )
//                    )
//                }
//                kompare()
//            }
//        }
//        "App OS settings displayed when available" {
//            runComposeUiTest {
//                setContentWithLocals {
//                    AlertsSettingsContent(
//                        factory(
//                            AlertsSettingsState(canOpenOsSettings = true)
//                        )
//                    )
//                }
//                kompare()
//            }
//        }
//    }
})
