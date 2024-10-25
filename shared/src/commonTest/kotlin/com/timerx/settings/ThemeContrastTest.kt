package com.timerx.settings

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ThemeContrastTest : FreeSpec({
    "default" {
        ThemeContrast.default.value shouldBe 0.0
    }
    "in rage" - {
        "lower" - { ThemeContrast(-1.0).value shouldBe -1.0 }
        "upper" - { ThemeContrast(1.0).value shouldBe 1.0 }
    }
    "out of range" - {
        "below" - {
            shouldThrowWithMessage<IllegalStateException>("ThemeContrast -1.01 must be between -1.0 and 1.0") {
                ThemeContrast(-1.01)
            }
        }
        "above" - {
            shouldThrowWithMessage<IllegalStateException>("ThemeContrast 1.01 must be between -1.0 and 1.0") {
                ThemeContrast(1.01)
            }
        }
    }
})
