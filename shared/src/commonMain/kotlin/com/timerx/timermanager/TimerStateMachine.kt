package com.timerx.timermanager

import androidx.compose.ui.graphics.Color
import com.timerx.sound.Beep
import com.timerx.sound.IntervalSound
import com.timerx.timermanager.TimerState.Running
import com.timerx.vibration.Vibration
import kotlinx.coroutines.flow.StateFlow

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

enum class TimerState {
    Running,
    Paused,
    Finished
}

data class RunState(
    val timerName: String = "",
    val timerState: TimerState = Running,

    val setIndex: Int = 0,
    val setCount: Int = 0,

    val repetitionIndex: Int = 0,
    val setRepetitionCount: Int = 0,

    val intervalIndex: Int = 0,

    val intervalRepetition: Int = 0,
    val intervalRepetitionCount: Int = 0,

    val intervalCount: Int = 0,
    val intervalName: String = "",

    val elapsed: Long = 0,
    val intervalDuration: Long = 0,

    val backgroundColor: Color = Color.Transparent,
    val displayCountAsUp: Boolean = false,
    val manualNext: Boolean = false
)

interface TimerStateMachine {

    val eventState: StateFlow<TimerEvent>

    fun pause()

    fun nextInterval()

    fun previousInterval()

    fun destroy()

    fun resume()
}
