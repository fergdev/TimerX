package com.intervallum.domain

private const val SECONDS_IN_MINUTE = 60L
private const val MINUTES_IN_HOUR = 60L
private const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
private const val SINGLE_DIGIT_MODULO = 10L

fun Long.timeFormatted(): String {
    if (this < 0L) throw IllegalStateException("Time cannot be negative")
    val hours = this / SECONDS_IN_HOUR
    val hoursString = if (hours == 0L) {
        ""
    } else {
        "$hours"
    }

    val minutes = this / MINUTES_IN_HOUR - hours * MINUTES_IN_HOUR
    val minutesString = if (minutes == 0L) {
        "00"
    } else if (minutes < SINGLE_DIGIT_MODULO) {
        "0$minutes"
    } else {
        "$minutes"
    }

    val seconds = this % SECONDS_IN_MINUTE
    val secondsString = if (seconds == 0L) {
        "00"
    } else if (seconds < SINGLE_DIGIT_MODULO) {
        "0$seconds"
    } else {
        "$seconds"
    }
    return if (hours == 0L) "$minutesString:$secondsString"
    else "$hoursString:$minutesString:$secondsString"
}
