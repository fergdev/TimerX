package com.intervallum.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class TimerTest : FreeSpec({
    "constructor" - {
        "valid throws no error" {
            shouldNotThrowAny {
                timer { timerSet { interval {} } }
            }
        }
        "throws when sets are empty" {
            shouldThrow<IllegalArgumentException> {
                Timer(name = "", sets = listOf(), createdAt = Instant.DISTANT_PAST)
            }.apply { message shouldBe "Timer must have at least one set" }
        }
        "throws when completed count is negative" {
            shouldThrow<IllegalArgumentException> {
                timer {
                    completedCount = -1
                    timerSet { interval {} }
                }
            }.apply { message shouldBe "Completed count cannot be negative completedCount=-1" }
        }
        "throws when started count is negative" {
            shouldThrow<IllegalArgumentException> {
                timer {
                    startedCount = -1
                    timerSet { interval {} }
                }
            }.apply { message shouldBe "Started count cannot be negative startedCount=-1" }
        }
    }
})
