package com.timerx.timermanager

import kotlinx.coroutines.flow.StateFlow

interface TimerStateMachine {

    val eventState: StateFlow<TimerEvent>

    fun pause()

    fun nextInterval()

    fun previousInterval()

    fun destroy()

    fun resume()
}
