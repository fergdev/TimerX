package com.timerx.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FinalCountDownTest : FreeSpec({
    "constructor" - {
        "valid does not throw" {
            shouldNotThrowAny {
                FinalCountDown()
            }
        }
        "valid does not throw 1000" {
            shouldNotThrowAny {
                FinalCountDown(duration = 1000L)
            }
        }
        "throws when duration is negative" {
            shouldThrow<IllegalArgumentException> {
                FinalCountDown(duration = -1)
            }.apply { message shouldBe "Final count down duration cannot be negative -1" }
        }
    }
})
