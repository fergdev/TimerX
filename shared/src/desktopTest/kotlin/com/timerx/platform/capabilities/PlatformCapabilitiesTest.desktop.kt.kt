package com.timerx.platform.capabilities

import com.timerx.platform.platformCapabilities
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PlatformCapabilitiesTest : FreeSpec({
    "default values" {
        with(platformCapabilities) {
            canSystemDynamicTheme shouldBe false
            canVibrate shouldBe false
            hasAnalytics shouldBe false
            hasOwnSplashScreen shouldBe false
        }
    }
})
