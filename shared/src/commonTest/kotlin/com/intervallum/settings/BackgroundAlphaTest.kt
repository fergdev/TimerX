package com.intervallum.settings

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BackgroundAlphaTest : FreeSpec({
    "default" { BackgroundAlpha.default.value shouldBe 0.2f }
    "in rage" - {
        "lower" - { BackgroundAlpha(0.0f).value shouldBe 0.0f }
        "upper" - { BackgroundAlpha(0.99f).value shouldBe 0.99f }
    }
    "out of range" - {
        "below" - {
            shouldThrowWithMessage<IllegalArgumentException>("Alpha -0.01 must be between 0.0..0.99") {
                BackgroundAlpha(-0.01F)
            }
        }
        "above" - {
            shouldThrowWithMessage<IllegalArgumentException>("Alpha 1.0 must be between 0.0..0.99") {
                BackgroundAlpha(1F)
            }
        }
    }
    "range" {
        BackgroundAlpha.range shouldBe 0.0f..0.99f
    }
})
