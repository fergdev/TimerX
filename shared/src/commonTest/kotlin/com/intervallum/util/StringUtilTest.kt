package com.intervallum.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class StringUtilTest : FreeSpec({
    "Capitalize" - {
        "should capitalize first letter" {
            "default".capitalize() shouldBe "Default"
        }
        "keep capitalized" {
            "Default".capitalize() shouldBe "Default"
        }
        "does no capitalize number" {
            "1default".capitalize() shouldBe "1default"
        }
    }
    "is valid" - {
        "null should be invalid" {
            null.isValid() shouldBe false
        }
        "'null' should be invalid" {
            "null".isValid() shouldBe false
        }
        "'Null' should be invalid" {
            "Null".isValid() shouldBe false
        }
        "'' should be invalid" {
            "".isValid() shouldBe false
        }
        "' ' should be invalid" {
            " ".isValid() shouldBe false
        }
        "'test' should be valid" {
            "test".isValid() shouldBe true
        }
    }
    "take if valid" - {
        "null should be null" {
            null.takeIfValid() shouldBe null
        }
        "'null' should be null" {
            "null".takeIfValid() shouldBe null
        }
        "'test' should be 'test'" {
            "test".takeIfValid() shouldBe "test"
        }
    }
})
