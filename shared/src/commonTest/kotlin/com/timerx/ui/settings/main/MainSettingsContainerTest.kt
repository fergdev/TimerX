package com.timerx.ui.settings.main

import app.cash.turbine.test
import com.timerx.BuildFlags
import com.timerx.settings.TimerXSettings
import com.timerx.testutil.TestConfigurationFactory
import com.timerx.testutil.asUnconfined
import com.timerx.testutil.idle
import com.timerx.ui.settings.main.MainSettingsIntent.KeepScreenOn
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import pro.respawn.flowmvi.test.subscribeAndTest

private fun mainSettingsState(isKeepScreenOn: Boolean = false) = MainSettingsState(
    isKeepScreenOn = isKeepScreenOn,
    privacyPolicyUri = BuildFlags.privacyPolicyUrl
)

class MainSettingsContainerTest : FreeSpec({
    asUnconfined()
    val timerXSettings = mock<TimerXSettings> {
        every { keepScreenOn } returns flowOf(true)
        everySuspend { setKeepScreenOn(any()) } returns Unit
    }

    val factory = {
        MainSettingsContainer(TestConfigurationFactory, timerXSettings)
    }

    "keep screen on" - {
        "default" - {
            factory().store.subscribeAndTest {
                states.test {
                    awaitItem() shouldBe mainSettingsState(isKeepScreenOn = false)
                    expectNoEvents()
                }
            }
        }
        "true" {
            factory().store.subscribeAndTest {
                states.test {
                    awaitItem()
                    awaitItem() shouldBe mainSettingsState(isKeepScreenOn = true)
                }
            }
        }
        "false" {
            factory().store.subscribeAndTest {
                states.test {
                    awaitItem() shouldBe mainSettingsState(isKeepScreenOn = false)
                }
            }
        }
        "action invokes settings" {
            factory().store.subscribeAndTest {
                intent(KeepScreenOn(true))
                idle()
                verifySuspend { timerXSettings.setKeepScreenOn(true) }
            }
        }
    }
})
