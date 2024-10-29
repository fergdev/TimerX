package com.timerx.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

private const val EPOCH_SECONDS = 10_000_000_000

class InstantUtilTest : FreeSpec({
    val now = Instant.fromEpochSeconds(EPOCH_SECONDS)
    val clock = object : Clock {
        override fun now() = now
    }
    "to time distance" - {
        "now" {
            now.toTimeDistance(clock) shouldBe "0 seconds"
        }
        "now with system clock" {
            Clock.System.now().toTimeDistance() shouldBe "0 seconds"
        }
        "10 seconds " {
            now.minus(10, DateTimeUnit.SECOND).toTimeDistance(clock) shouldBe "10 seconds"
        }
        "1 minute " {
            now.minus(1, DateTimeUnit.MINUTE).toTimeDistance(clock) shouldBe "1 minute"
        }
        "10 minutes " {
            now.minus(10, DateTimeUnit.MINUTE).toTimeDistance(clock) shouldBe "10 minutes"
        }
        "1 hour " {
            now.minus(1, DateTimeUnit.HOUR).toTimeDistance(clock) shouldBe "1 hour"
        }
        "10 hours " {
            now.minus(10, DateTimeUnit.HOUR).toTimeDistance(clock) shouldBe "10 hours"
        }
        "1 day " {
            now.minus(DatePeriod(days = 1), TimeZone.UTC).toTimeDistance(clock) shouldBe "1 day"
        }
        "10 days " {
            now.minus(DatePeriod(days = 10), TimeZone.UTC).toTimeDistance(clock) shouldBe "10 days"
        }
    }
    "to ago appends ago" {
        now.toAgo(clock) shouldBe "0 seconds ago"
    }
})
