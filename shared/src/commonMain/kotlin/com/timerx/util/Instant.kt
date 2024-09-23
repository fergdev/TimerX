package com.timerx.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun Instant.toAgo() = "${toTimeDistance()} ago"

fun Instant.toTimeDistance(): String {
    val now = Clock.System.now()
    val duration = now.minus(this)
    val inWholeDays = duration.inWholeDays
    if (inWholeDays != 0L) {
        return if (inWholeDays == 1L) "1 day"
        else "$inWholeDays days"
    }

    val inWholeHours = duration.inWholeHours
    if (inWholeHours != 0L) {
        return if (inWholeHours == 1L) "1 hour"
        else "$inWholeHours hours"
    }
    val inWholeMinutes = duration.inWholeMinutes
    if (inWholeMinutes != 0L) {
        return if (inWholeMinutes == 1L) "1 minute"
        else "$inWholeMinutes minutes"
    }
    val inWholeSeconds = duration.inWholeSeconds
    if (inWholeSeconds != 0L) return "$inWholeSeconds seconds"
    return "1 second"
}
