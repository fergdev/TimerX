package com.timerx.ui.settings.alerts

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.kompare.kompare
import com.timerx.settings.VibrationSetting.CanVibrate
import com.timerx.testutil.NotAndroidCondition
import com.timerx.testutil.createComponent
import com.timerx.testutil.setContentWithLocals
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store

class FakeAlertSettingsContainer(private val state: AlertsSettingsState = AlertsSettingsState()) :
    Container<AlertsSettingsState, AlertsSettingsIntent, Nothing> {
    override val store: Store<AlertsSettingsState, AlertsSettingsIntent, Nothing>
        get() = store(state) {}
}

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@OptIn(ExperimentalTestApi::class)
@EnabledIf(NotAndroidCondition::class)
class AlertSettingsContentTest : FreeSpec({
    val factory = { alertSettingsState: AlertsSettingsState ->
        createComponent {
            DefaultAlertSettingsComponent(
                onBackClicked = {},
                factory = { FakeAlertSettingsContainer(alertSettingsState) },
                context = it
            )
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
