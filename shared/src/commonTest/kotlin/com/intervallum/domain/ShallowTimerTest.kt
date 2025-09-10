package com.intervallum.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ShallowTimerTest : FreeSpec({
    "constructor" - {
        "valid does not throw" {
            shouldNotThrowAny {
                ShallowTimer(name = "test")
            }
        }
        "started count cannot be negative" {
            shouldThrow<IllegalArgumentException> {
                ShallowTimer(
                    name = "test",
                    startedCount = -1,
                )
            }.apply {
                message shouldBe "startedCount=-1 cannot be negative"
            }
        }
        "completed count cannot be negative" {
            shouldThrow<IllegalArgumentException> {
                ShallowTimer(
                    name = "test", completedCount = -1,
                )
            }.apply {
                message shouldBe "completedCount=-1 cannot be negative"
            }
        }
        "duration must be greater than 0" {
            shouldThrow<IllegalArgumentException> {
                ShallowTimer(name = "test", duration = 0L)
            }.apply {
                message shouldBe "duration=0 must be greater than 0"
            }
        }
    }
})
