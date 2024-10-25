package com.timerx.settings

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import com.timerx.domain.SortTimersBy.NAME_ASC
import com.timerx.domain.SortTimersBy.SORT_ORDER
import com.timerx.platform.PlatformCapabilities
import com.timerx.platform.platformCapabilitiesOf
import com.timerx.util.awaitAndExpectNoMore
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers

class TimerXSettingsImpTest : FreeSpec({
    val settings = MapSettings()
    val timerXSettingsFactory: (PlatformCapabilities) -> TimerXSettingsImpl =
        { platformCapabilities ->
            TimerXSettingsImpl(
                settings = settings,
                platformCapabilities = platformCapabilities,
                dispatcher = Dispatchers.Unconfined
            )
        }
    val defaultSettingsFactory = { timerXSettingsFactory(platformCapabilitiesOf()) }
    afterTest { settings.clear() }

    "sort timers by" - {
        "default" {
            defaultSettingsFactory().sortTimersBy.test {
                awaitAndExpectNoMore() shouldBe SORT_ORDER
            }
        }
        "from settings" {
            defaultSettingsFactory().setSortTimersBy(NAME_ASC)
            defaultSettingsFactory().sortTimersBy.test {
                awaitAndExpectNoMore() shouldBe NAME_ASC
            }
        }
    }
    "keep screen on" - {
        "default" {
            defaultSettingsFactory().keepScreenOn.test { awaitAndExpectNoMore() shouldBe true }
        }
        "from settings" {
            defaultSettingsFactory().setKeepScreenOn(false)
            defaultSettingsFactory().keepScreenOn.test { awaitAndExpectNoMore() shouldBe false }
        }
    }
    "analytics" - {
        "with platform analytics unavailable" {
            defaultSettingsFactory()
                .analytics
                .test { awaitAndExpectNoMore() shouldBe AnalyticsSettings.NotAvailable }
        }
        "with platform analytics available" - {
            "default" {
                timerXSettingsFactory(platformCapabilitiesOf(hasAnalytics = true))
                    .analytics
                    .test { awaitAndExpectNoMore() shouldBe AnalyticsSettings.Available(true) }
            }
            "with disabled" {
                val timerXSettings =
                    timerXSettingsFactory(platformCapabilitiesOf(hasAnalytics = true))
                timerXSettings.setCollectAnalytics(false)
                timerXSettings.analytics
                    .test { awaitAndExpectNoMore() shouldBe AnalyticsSettings.Available(false) }
            }
        }
    }
})
