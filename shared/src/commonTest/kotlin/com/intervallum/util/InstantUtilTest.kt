package com.intervallum.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

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
            now.minus(10.seconds).toTimeDistance(clock) shouldBe "10 seconds"
        }
        "1 minute " {
            now.minus(1.minutes).toTimeDistance(clock) shouldBe "1 minute"
        }
        "10 minutes " {
            now.minus(10.minutes).toTimeDistance(clock) shouldBe "10 minutes"
        }
        "1 hour " {
            now.minus(1.hours).toTimeDistance(clock) shouldBe "1 hour"
        }
        "10 hours " {
            now.minus(10.hours).toTimeDistance(clock) shouldBe "10 hours"
        }
        "1 day " {
            now.minus(1.days).toTimeDistance(clock) shouldBe "1 day"
        }
        "10 days " {
            now.minus(10.days).toTimeDistance(clock) shouldBe "10 days"
        }
    }
    "to ago appends ago" {
        now.toAgo(clock) shouldBe "0 seconds ago"
    }
})
