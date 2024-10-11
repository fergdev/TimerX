package com.timerx.timermanager

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class TimerManager(private val timerRepository: ITimerRepository) {
    private var timerStateMachine: TimerStateMachineImpl? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _eventState = MutableStateFlow<TimerEvent>(TimerEvent.Idle)
    val eventState: StateFlow<TimerEvent> = _eventState
    private var currentTimer: Timer? = null

    fun startTimer(timer: Timer) {
        currentTimer = timer
        initTimerStateMachine(timer)
    }

    private fun initTimerStateMachine(timer: Timer) {
        val timerStateMachine = TimerStateMachineImpl(timer, coroutineScope)
        this.timerStateMachine = timerStateMachine
        coroutineScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                _eventState.value = timerEvent
                when (timerEvent) {
                    is TimerEvent.Finished -> updateTimerFinishedStats(timer)
                    is TimerEvent.Started -> updateTimerStartStats(timer)
                    is TimerEvent.Destroy -> {
                        _eventState.value = TimerEvent.Idle
                    }

                    else -> {}
                }
            }
        }
    }

    fun restartCurrentTimer() {
        val timer =
            currentTimer ?: throw IllegalStateException("Attempting to restart with null timer")
        initTimerStateMachine(timer)
    }

    private fun updateTimerFinishedStats(timer: Timer) {
        coroutineScope.launch {
            timerRepository.updateTimerStats(
                timer.id,
                timer.startedCount,
                timer.completedCount + 1,
                timer.lastRun ?: Clock.System.now()
            )
        }
    }

    private fun updateTimerStartStats(timer: Timer) {
        coroutineScope.launch {
            timerRepository.updateTimerStats(
                timer.id,
                timer.startedCount + 1,
                timer.completedCount,
                Clock.System.now()
            )
        }
    }

    fun playPause() {
        if (timerStateMachine?.eventState?.value?.runState?.timerState == TimerState.Running) {
            timerStateMachine?.pause()
        } else if (timerStateMachine?.eventState?.value?.runState?.timerState == TimerState.Paused) {
            timerStateMachine?.resume()
        }
    }

    fun nextInterval() {
        timerStateMachine?.nextInterval()
    }

    fun previousInterval() {
        timerStateMachine?.previousInterval()
    }

    fun destroy() {
        timerStateMachine?.destroy()
        timerStateMachine = null
    }

    fun isRunning() = timerStateMachine != null
}
