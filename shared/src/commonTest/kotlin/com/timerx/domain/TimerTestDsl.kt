@file:Suppress("MemberVisibilityCanBePrivate")

package com.timerx.domain

import androidx.compose.ui.graphics.Color
import com.timerx.sound.Beep
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Instant

class TimerBuilder {
    var id: Long = 0L
    var sortOrder: Long = NO_SORT_ORDER
    var name: String = "test"
    var sets: List<TimerSet> = listOf()
    var finishColor: Color = Color.Red
    var finishBeep: Beep = Beep.Alert
    var finishVibration: Vibration = Vibration.Heavy
    var duration: Long = sets.length()
    var startedCount: Long = 0
    var completedCount: Long = 0
    var createdAt: Instant = Instant.DISTANT_PAST
    var lastRun: Instant? = null
    fun build(): Timer =
        Timer(
            id = id,
            sortOrder = sortOrder,
            name = name,
            sets = sets,
            finishColor = finishColor,
            finishBeep = finishBeep,
            finishVibration = finishVibration,
            duration = duration,
            startedCount = startedCount,
            completedCount = completedCount,
            createdAt = createdAt,
            lastRun = lastRun
        )
}

fun timer(block: TimerBuilder.() -> Unit) = TimerBuilder().apply(block).build()

class TimerSetBuilder {
    var id: Long = 0L
    var repetitions: Int = 1
    var intervals: List<TimerInterval> = listOf()
    fun build() = TimerSet(id, repetitions, intervals.toPersistentList())
}

fun TimerBuilder.timerSet(block: TimerSetBuilder.() -> Unit) {
    sets += TimerSetBuilder().apply(block).build()
}

class TimerIntervalBuilder {
    var id: Long = 0L
    var name: String = "test"
    var duration: Long = 0L
    var color: Color = Color.Blue
    var skipOnLastSet: Boolean = false
    var countUp: Boolean = false
    var manualNext: Boolean = false
    var textToSpeech: Boolean = true
    var beep: Beep = Beep.Alert
    var vibration: Vibration = Vibration.Medium
    var finalCountDown: FinalCountDown = FinalCountDown()
    fun build() = TimerInterval(
        id = id,
        name = name,
        duration = duration,
        color = color,
        skipOnLastSet = skipOnLastSet,
        countUp = countUp,
        manualNext = manualNext,
        textToSpeech = textToSpeech,
        beep = beep,
        vibration = vibration,
        finalCountDown = finalCountDown,
    )
}

fun TimerSetBuilder.interval(block: TimerIntervalBuilder.() -> Unit) {
    intervals += TimerIntervalBuilder().apply(block).build()
}
