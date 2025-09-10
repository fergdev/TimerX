package com.intervallum.domain

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

class LengthTest : FreeSpec({
    "interval" {
        timerInterval().length() shouldBe 1000L
    }

    "set" {
        TimerSet(intervals = timerIntervalList(5)).length() shouldBe 15000L
    }

    "set list" {
        timerSetList(5).length() shouldBe 75000L
    }

    "timer" {
        Timer(
            name = "test",
            createdAt = Instant.fromEpochMilliseconds(0),
            sets = timerSetList(5)
        ).length() shouldBe 75000L
    }
})
