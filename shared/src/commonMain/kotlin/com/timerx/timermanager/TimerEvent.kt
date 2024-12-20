package com.timerx.timermanager

import com.timerx.sound.Beep
import com.timerx.sound.IntervalSound
import com.timerx.vibration.Vibration

sealed interface TimerEvent {
    val runState: RunState

    data class Started(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class NextInterval(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class PreviousInterval(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class Finished(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class Paused(override val runState: RunState) : TimerEvent
    data class Resumed(override val runState: RunState) : TimerEvent

    data class Ticker(
        override val runState: RunState,
        val beep: Beep? = null,
        val vibration: Vibration? = null
    ) : TimerEvent

    data class Destroy(override val runState: RunState = RunState()) : TimerEvent
}
