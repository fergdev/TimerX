package com.timerx.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Instant

private fun timerInterval(multiplier: Int = 1) = TimerInterval(
    name = "test",
    duration = 1000L * multiplier
)

private fun timerIntervalList(count: Int) = MutableList(count) {
    timerInterval(it + 1)
}.toPersistentList()

private fun timerSetList(count: Int) = MutableList(count) {
    TimerSet(intervals = timerIntervalList(5))
}

class TimerTest : FreeSpec({
    "length" - {
        "interval" {
            timerInterval().length() shouldBe 1000L
        }

        "set" {
            TimerSet(intervals = timerIntervalList(5)).length() shouldBe 15000L
        }

        "set list" {
            timerSetList(5).length() shouldBe 75000L
        }

        "set list" {
            Timer(
                name = "test",
                createdAt = Instant.fromEpochMilliseconds(0),
                sets = timerSetList(5)
            ).length() shouldBe 75000L
        }
    }

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
