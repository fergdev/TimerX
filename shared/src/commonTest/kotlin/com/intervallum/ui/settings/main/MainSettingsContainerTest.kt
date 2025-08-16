package com.intervallum.ui.settings.main

import app.cash.turbine.test
import com.intervallum.BuildFlags
import com.intervallum.settings.IntervallumSettings
import com.intervallum.testutil.TestConfigurationFactory
import com.intervallum.testutil.asUnconfined
import com.intervallum.testutil.idle
import com.intervallum.ui.settings.main.MainSettingsIntent.KeepScreenOn
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
    val intervallumSettings = mock<IntervallumSettings> {
        every { keepScreenOn } returns flowOf(true)
        everySuspend { setKeepScreenOn(any()) } returns Unit
    }

    val factory = {
        MainSettingsContainer(TestConfigurationFactory, intervallumSettings)
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
                verifySuspend { intervallumSettings.setKeepScreenOn(true) }
            }
        }
    }
})
