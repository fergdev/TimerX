package com.timerx.timermanager

import com.timerx.domain.Timer
import kotlinx.coroutines.flow.StateFlow

interface TimerManager {
    val eventState: StateFlow<TimerEvent>
    fun startTimer(timer: Timer)
    fun playPause()
    fun nextInterval()
    fun previousInterval()
    fun destroy()
    fun isRunning(): Boolean
}