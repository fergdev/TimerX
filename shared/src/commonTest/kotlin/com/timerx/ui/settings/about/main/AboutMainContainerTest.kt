package com.timerx.ui.settings.about.main

import app.cash.turbine.test
import com.timerx.contact.ContactProvider
import com.timerx.settings.AnalyticsSettings.Available
import com.timerx.settings.TimerXSettings
import com.timerx.util.asUnconfined
import com.timerx.util.idle
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import pro.respawn.flowmvi.test.subscribeAndTest

class AboutMainContainerTest : FreeSpec({
    asUnconfined()
    val timerXSettings = mock<TimerXSettings>()
    val contactProvider = mock<ContactProvider>()
    afterEach {
        resetAnswers(timerXSettings, contactProvider)
    }
    val aboutMainContainer = {
        AboutMainContainer(
            timerXSettings = timerXSettings,
            contactProvider = contactProvider,
        )
    }

    "init" - {
        "sets default state" {
            aboutMainContainer().store.subscribeAndTest {
                states.test {
                    awaitItem() shouldBe AboutMainState()
                    expectNoEvents()
                }
            }
        }
    }
    "collect settings" {
        everySuspend { timerXSettings.analytics } returns flowOf(Available(true))
        aboutMainContainer().store.subscribeAndTest {
            states.test {
                awaitItem()
                awaitItem() shouldBe AboutMainState(analyticsSettings = Available(true))
                expectNoEvents()
            }
        }
    }
    "intent" - {
        "set collect analytics invokes settings" {
            mock<TimerXSettings> {
                everySuspend { timerXSettings.analytics } returns flowOf(Available(true))
                everySuspend { timerXSettings.setCollectAnalytics(any()) } returns Unit
            }
            aboutMainContainer().store.subscribeAndTest {
                intent(AboutMainIntent.UpdateCollectAnalytics(true))
                idle()
                verifySuspend { timerXSettings.setCollectAnalytics(true) }
            }
        }
        "contact invokes contact provider" {
            mock<ContactProvider> { every { contactProvider.contact() } returns Unit }
            everySuspend { timerXSettings.analytics } returns flowOf(Available(true))
            aboutMainContainer().store.subscribeAndTest {
                intent(AboutMainIntent.ContactSupport)
                idle()
                verifySuspend { contactProvider.contact() }
            }
        }
    }
})