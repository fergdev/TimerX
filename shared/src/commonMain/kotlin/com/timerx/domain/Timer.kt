package com.timerx.domain

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

data class Timer(
    val id: String = "",
    val name: String,
    val sets: ImmutableList<TimerSet>,
    val finishColor: Color = Color.Red
)

data class TimerSet(
    val id: String = "",
    val repetitions: Long = 1,
    val intervals: ImmutableList<TimerInterval>
)

data class TimerInterval(
    val id: String = "",
    val name: String,
    val duration: Long,
    val color: Color = Color.Blue
)

fun Long.formatted(): String {
    val hours = this / (60L * 60L)
    val hoursString = if (hours == 0L) {
        ""
    } else {
        "$hours:"
    }

    val minutes = this / 60L
    val minutesString = if (minutes == 0L) {
        "00"
    } else if (minutes < 10L) {
        "0$minutes"
    } else {
        "$minutes"
    }

    val seconds = this % 60L
    val secondsString = if (seconds == 0L) {
        "00"
    } else if (seconds < 10L) {
        "0$seconds"
    } else {
        "$seconds"
    }

    return "$hoursString$minutesString:$secondsString"
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
    return duration
}