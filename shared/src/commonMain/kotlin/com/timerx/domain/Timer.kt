package com.timerx.domain

import androidx.compose.ui.graphics.Color
import com.timerx.beep.Beep
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant

internal const val NO_SORT_ORDER = -1L

data class Timer(
    val id: Long = 0L,
    val sortOrder: Long = NO_SORT_ORDER,
    val name: String,
    val sets: ImmutableList<TimerSet>,
    val finishColor: Color = Color.Red,
    val finishBeep: Beep = Beep.Alert,
    val finishVibration: Vibration = Vibration.Heavy,
    val startedCount: Long = 0,
    val completedCount: Long = 0,
    val lastRun: Instant? = null
)

data class TimerSet(
    val id: Long = 0L,
    val repetitions: Int = 1,
    val intervals: ImmutableList<TimerInterval>
)

data class TimerInterval(
    val id: Long = 0L,
    val name: String,
    val duration: Int,
    val color: Color = Color.Blue,
    val skipOnLastSet: Boolean = false,
    val countUp: Boolean = false,
    val manualNext: Boolean = false,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Medium,
    val finalCountDown: FinalCountDown = FinalCountDown()
)

data class FinalCountDown(
    val duration: Int = 3,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Light
)

fun Int.timeFormatted(): String {
    val hours = this / (SECONDS_IN_MINUTE * MINUTES_IN_HOUR)
    val hoursString = if (hours == 0L) {
        ""
    } else {
        "$hours:"
    }

    val minutes = this / MINUTES_IN_HOUR
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

    return "$hoursString$minutesString:$secondsString"
}

private const val SECONDS_IN_MINUTE = 60L
private const val MINUTES_IN_HOUR = 60L
private const val SINGLE_DIGIT_MODULO = 10L

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
