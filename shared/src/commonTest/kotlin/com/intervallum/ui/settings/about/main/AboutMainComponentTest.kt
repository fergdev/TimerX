package com.intervallum.ui.settings.about.main

import com.intervallum.contact.ContactProvider
import com.intervallum.settings.AnalyticsSettings
import com.intervallum.settings.AnalyticsSettings.Available
import com.intervallum.settings.IntervallumSettings
import com.intervallum.testutil.createComponent
import com.intervallum.testutil.defaultIdle
import com.intervallum.testutil.testDispatchers
import com.intervallum.ui.settings.about.aboutlibs.AboutLibsComponent
import com.intervallum.ui.settings.about.changelog.ChangeLogComponent
import com.intervallum.ui.settings.about.main.AboutMainState.AnalyticsNotSupported
import com.intervallum.ui.settings.about.main.AboutMainState.AnalyticsSupported
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import pro.respawn.flowmvi.util.typed

class AboutMainComponentTest : FreeSpec({
    val intervallumSettings: IntervallumSettings = mock<IntervallumSettings>()
    val contactProvider: ContactProvider = mock<ContactProvider>()
    val testDispatchers = testDispatchers()

    beforeTest { resetAnswers(intervallumSettings, contactProvider) }

    val factory = {
        createComponent {
            DefaultAboutMainComponent(
                componentContext = it,
                onBack = {},
                intervallumSettings = intervallumSettings,
                contactProvider = contactProvider,
                txDispatchers = testDispatchers
            )
        }
    }

    "init" - {
        "default" {
            factory().state.value shouldBe AnalyticsNotSupported()
        }
        "analytics not supported" {
            every { intervallumSettings.analytics } returns flowOf(AnalyticsSettings.NotAvailable)
            with(factory()) {
                testDispatchers.defaultIdle()
                state.value shouldBe AnalyticsNotSupported()
            }
        }
        "analytics supported enabled" {
            every { intervallumSettings.analytics } returns flowOf(Available(true))
            with(factory()) {
                testDispatchers.defaultIdle()
                val analyticsSupported =
                    state.value.typed<AnalyticsSupported>()!!

                analyticsSupported.collectAnalyticsEnable shouldBe true
            }
        }
        "analytics supported disabled" {
            every { intervallumSettings.analytics } returns flowOf(Available(false))
            with(factory()) {
                testDispatchers.defaultIdle()
                state.value.typed<AnalyticsSupported>()!!.collectAnalyticsEnable shouldBe false
            }
        }
        "analytics call back invokes intent" {
            every { intervallumSettings.analytics } returns flowOf(Available(false))
            everySuspend { intervallumSettings.setCollectAnalytics(true) } returns Unit

            with(factory()) {
                testDispatchers.defaultIdle()
                val analyticsSupported =
                    state.value.typed<AnalyticsSupported>()!!

                analyticsSupported.updateCollectAnalytics(true)
                testDispatchers.defaultIdle()
            }
            verifySuspend { intervallumSettings.setCollectAnalytics(true) }
        }
    }
    "contact support invokes provider" {
        every { contactProvider.contactSupport() } returns Unit
        with(factory()) { contactSupport() }
        verify { contactProvider.contactSupport() }
    }
    "about libs clicked" - {
        "fills slot when empty" {
            with(factory()) {
                onAboutLibsClicked()
                aboutLibsSlot.value.child?.instance shouldBe AboutLibsComponent
            }
        }
        "fills slot when not empty " {
            with(factory()) {
                onAboutLibsClicked()
                onAboutLibsClicked()
                aboutLibsSlot.value.child?.instance shouldBe AboutLibsComponent
            }
        }
    }
    "on dismiss libs" - {
        "empty slots stays empty" {
            with(factory()) {
                onDismissLibs()
                aboutLibsSlot.value.child?.instance shouldBe null
            }
        }
        "filled slots is emptied" {
            with(factory()) {
                onAboutLibsClicked()
                onDismissLibs()
                aboutLibsSlot.value.child?.instance shouldBe null
            }
        }
    }
    "on change log" - {
        "fills slot" {
            with(factory()) {
                onChangeLog()
                changeLogSlot.value.child?.instance shouldBe ChangeLogComponent
            }
        }
        "fills slot when not empty" {
            with(factory()) {
                onChangeLog()
                onChangeLog()
                changeLogSlot.value.child?.instance shouldBe ChangeLogComponent
            }
        }
    }
    "on dismiss change log" - {
        "empty slot remains empty" {
            with(factory()) {
                onDismissChangeLog()
                changeLogSlot.value.child?.instance shouldBe null
            }
        }
        "filled slot gets emptied" {
            with(factory()) {
                onChangeLog()
                onDismissChangeLog()
                aboutLibsSlot.value.child?.instance shouldBe null
            }
        }
    }
})
