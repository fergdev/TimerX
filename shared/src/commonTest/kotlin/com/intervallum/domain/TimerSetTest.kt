package com.intervallum.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TimerSetTest : FreeSpec({
    "constructor" - {
        "valid does not throw" {
            shouldNotThrowAny {
                TimerSet(intervals = listOf(TimerInterval(name = "", duration = 1000L)))
            }
        }
        "empty intervals throws exception" {
            shouldThrow<IllegalArgumentException> {
                TimerSet(intervals = listOf())
            }.apply {
                message shouldBe "Timer set must have at least one interval"
            }
        }
    }
})
