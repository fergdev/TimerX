package com.timerx.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun Instant.toAgo(): String {
    val now = Clock.System.now()
    val duration = now.minus(this)
    val inWholeDays = duration.inWholeDays
    if (inWholeDays != 0L) {
        if (inWholeDays == 1L) return "1 day"
        return "$inWholeDays days"
    }

    val inWholeHours = duration.inWholeHours
    if (inWholeHours != 0L) {
        if (inWholeHours == 1L) return "1 hour"
        return "$inWholeHours hours"
    }
    val inWholeMinutes = duration.inWholeMinutes
    if (inWholeMinutes != 0L) {
        if (inWholeMinutes == 1L) return "1 minute"
        return "$inWholeMinutes minutes"
    }
    val inWholeSeconds = duration.inWholeSeconds
    if (inWholeSeconds != 0L) return "$inWholeSeconds seconds"
    return "1 second"
}