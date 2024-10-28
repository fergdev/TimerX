package com.timerx.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TimerIntervalTest : FreeSpec({
    "constructor" - {
        "valid does not throw" {
            shouldNotThrowAny {
                TimerInterval(name = "test", duration = 1L)
            }
        }
        "negative duration should throw" {
            shouldThrow<IllegalArgumentException> {
                TimerInterval(name = "test", duration = -1L)
            }.apply {
                message shouldBe "Duration must be greater than zero duration=-1"
            }
        }
    }
})
