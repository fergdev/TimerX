package com.intervallum.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TimeFormattedTest : FreeSpec({
    "time formatted" - {
        "0" {
            0L.timeFormatted() shouldBe "00:00"
        }
        "-1" {
            shouldThrow<IllegalStateException> {
                (-1L).timeFormatted()
            }.apply { message shouldBe "Time cannot be negative" }
        }
        "1" {
            1L.timeFormatted() shouldBe "00:01"
        }
        "10" {
            10L.timeFormatted() shouldBe "00:10"
        }
        "100" {
            100L.timeFormatted() shouldBe "01:40"
        }
        "1_000" {
            1_000L.timeFormatted() shouldBe "16:40"
        }
        "10_000" {
            10_000L.timeFormatted() shouldBe "2:46:40"
        }
        "100_000" {
            100_000L.timeFormatted() shouldBe "27:46:40"
        }
        "1_000_000" {
            1_000_000L.timeFormatted() shouldBe "277:46:40"
        }
        "10_000_000" {
            10_000_000L.timeFormatted() shouldBe "2777:46:40"
        }
    }
})
