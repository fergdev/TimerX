package com.timerx.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun Instant.toAgo(clock: Clock = Clock.System) = "${toTimeDistance(clock)} ago"

fun Instant.toTimeDistance(clock: Clock = Clock.System): String {
    val duration = clock.now().minus(this)
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
    return "${duration.inWholeSeconds} seconds"
}
