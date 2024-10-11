package com.timerx.background

import co.touchlab.kermit.Logger
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSTimer
import kotlin.experimental.ExperimentalNativeApi

class BackgroundManager(private val timerManager: TimerManager) {

    private var timer: NSTimer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        coroutineScope.launch {
            timerManager.eventState.collect {
                if (it.shouldCreateTimer()) {
                    Logger.v { "Keep alive timer start" }
                    createTimer()
                } else if (it.shouldDestroyTimer()) {
                    Logger.v { "Keep alive timer end" }
                    timer?.invalidate()
                    timer = null
                }
            }
        }
    }

    @OptIn(ExperimentalNativeApi::class)
    private fun createTimer() {
        assert(timer == null)
        timer = NSTimer.scheduledTimerWithTimeInterval(1.0, true) {
            Logger.v { "Keep Alive ${it?.fireDate?.description()}" }
        }
    }

    private fun TimerEvent.shouldCreateTimer() =
        this is TimerEvent.Started || this is TimerEvent.Resumed

    private fun TimerEvent.shouldDestroyTimer() =
        this is TimerEvent.Idle || this is TimerEvent.Paused || this is TimerEvent.Finished
}