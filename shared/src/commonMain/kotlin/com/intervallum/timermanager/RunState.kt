package com.intervallum.timermanager

import androidx.compose.ui.graphics.Color
import com.intervallum.timermanager.TimerState.Running

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

enum class TimerState {
    Running,
    Paused,
    Finished
}
