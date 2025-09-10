package com.intervallum.platform

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PlatformCapabilitiesTest : FreeSpec({
    "default" {
        val platformCapabilities = com.intervallum.platform.platformCapabilities
        with(platformCapabilities) {
            canVibrate shouldBe true
            canSystemDynamicTheme shouldBe false
            hasAnalytics shouldBe true
            hasOwnSplashScreen shouldBe false
        }
    }
})
