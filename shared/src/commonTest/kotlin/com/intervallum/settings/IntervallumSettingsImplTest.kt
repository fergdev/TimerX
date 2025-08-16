package com.intervallum.settings

import app.cash.turbine.test
import com.intervallum.domain.SortTimersBy.NAME_ASC
import com.intervallum.domain.SortTimersBy.SORT_ORDER
import com.intervallum.platform.PlatformCapabilities
import com.intervallum.platform.platformCapabilitiesOf
import com.intervallum.testutil.awaitAndExpectNoMore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalSettingsApi::class)
class IntervallumSettingsImplTest : FreeSpec({
    val settings = MapSettings().makeObservable().toFlowSettings()
    val intervallumSettingsFactory: (PlatformCapabilities) -> IntervallumSettingsImpl =
        { platformCapabilities ->
            IntervallumSettingsImpl(
                flowSettings = settings,
                platformCapabilities = platformCapabilities,
            )
        }
    val defaultSettingsFactory = { intervallumSettingsFactory(platformCapabilitiesOf()) }
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
        "with platform analytics unavailable" - {
            "default" {
                defaultSettingsFactory()
                    .analytics
                    .test { awaitAndExpectNoMore() shouldBe AnalyticsSettings.NotAvailable }
            }
            "throws when set" {
                val intervallumSettings = defaultSettingsFactory()
                shouldThrow<IllegalArgumentException> {
                    intervallumSettings.setCollectAnalytics(false)
                }.apply { message shouldBe "Analytics are not available on this platform" }
            }
        }
        "with platform analytics available" - {
            "default" {
                intervallumSettingsFactory(platformCapabilitiesOf(hasAnalytics = true))
                    .analytics
                    .test { awaitAndExpectNoMore() shouldBe AnalyticsSettings.Available(true) }
            }
            "with disabled" {
                val intervallumSettings =
                    intervallumSettingsFactory(platformCapabilitiesOf(hasAnalytics = true))
                intervallumSettings.setCollectAnalytics(false)
                intervallumSettings.analytics
                    .test { awaitAndExpectNoMore() shouldBe AnalyticsSettings.Available(false) }
            }
        }
    }
})
