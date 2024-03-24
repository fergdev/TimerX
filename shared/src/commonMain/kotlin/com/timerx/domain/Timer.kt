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
    val repetitions: Int = 1,
    val intervals: ImmutableList<TimerInterval>
)

data class TimerInterval(
    val id: String = "",
    val name: String,
    val duration: Int,
    val color: Color = Color.Blue,
    val skipOnLastSet: Boolean = false
)

fun Int.timeFormatted(): String {
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

fun Timer.length(): Int {
    return sets.fold(0) { acc, i ->
        acc + i.length()
    }
}

fun List<TimerSet>.length(): Int {
    return fold(0) { acc, i ->
        acc + i.length()
    }
}

fun TimerSet.length(): Int {
    return intervals.fold(0) { acc, i ->
        acc + i.length()
    } * repetitions
}

fun TimerInterval.length(): Int {
    return duration
}