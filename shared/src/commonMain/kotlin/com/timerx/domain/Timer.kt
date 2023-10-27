package com.timerx.domain

data class Timer(
    val id: Long = -1, val name: String, val sets: List<TimerSet>
)

data class TimerSet(
    val id: Long = -1, val repetitions: Long = 1, val intervals: List<TimerInterval>
)

data class TimerInterval(
    val id: Long = -1, val name: String, val repetitions: Long = 1, val duration: Long
)

fun Long.formatted(): String {
    val hours = this / (60L * 60L)
    val hoursString = if (hours == 0L) {
        ""
    } else {
        "$hours:"
    }

    val mins = this / 60L
    val minsString = if (mins == 0L) {
        "00"
    } else if (mins < 10L) {
        "0$mins"
    } else {
        "0$mins"
    }

    val secs = this % 60L
    val secsString = if (secs == 0L) {
        "00"
    } else if (secs < 10L) {
        "0$secs"
    } else {
        "$secs"
    }

    return "$hoursString$minsString:$secsString"
}

fun Timer.length(): Long {
    return sets.fold(0L) { acc, i ->
        acc + i.length()
    }
}

fun List<TimerSet>.length(): Long {
    return fold(0L) { acc, i ->
        acc + i.length()
    }
}

fun TimerSet.length(): Long {
    return intervals.fold(0L) { acc, i ->
        acc + i.length()
    } * repetitions
}

fun TimerInterval.length(): Long {
    return duration * repetitions
}