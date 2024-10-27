package com.timerx.settings

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AnalyticsSettingsTest : FreeSpec({
    "is enabled" - {
        "should be false when not available" {
            AnalyticsSettings.NotAvailable.isEnabled() shouldBe false
        }
        "should be false when available and not set" {
            AnalyticsSettings.Available(false).isEnabled() shouldBe false
        }
        "should be true when available and set" {
            AnalyticsSettings.Available(false).isEnabled() shouldBe false
        }
    }
})
