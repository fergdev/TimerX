package com.timerx.sound

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VolumeTest : FreeSpec({
    "default" - { Volume.default.value shouldBe 1.0f }
    "in range" - {
        "lower bound" { Volume(0.0f).value shouldBe 0.0f }
        "middle" { Volume(0.5f).value shouldBe 0.5f }
        "upper bound" { Volume(1.0f).value shouldBe 1.0f }
    }
    "out of range" - {
        "below" {
            shouldThrow<IllegalArgumentException> { Volume(-0.1f) }
                .message shouldBe "Invalid volume: -0.1"
        }
        "above" {
            shouldThrow<IllegalArgumentException> { Volume(-1.1f) }
                .message shouldBe "Invalid volume: -1.1"
        }
    }
})
