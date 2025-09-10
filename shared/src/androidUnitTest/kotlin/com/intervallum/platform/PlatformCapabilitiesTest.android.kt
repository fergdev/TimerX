package com.intervallum.platform

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PlatformCapabilitiesTest : FreeSpec({
    "default values" {
        with(platformCapabilities) {
            canSystemDynamicTheme shouldBe false
            canVibrate shouldBe true
            hasAnalytics shouldBe true
            hasOwnSplashScreen shouldBe true
        }
    }
})
