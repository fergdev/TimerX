package com.timerx.timermanager

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.timermanager.TimerState.Finished
import com.timerx.timermanager.TimerState.Paused
import com.timerx.timermanager.TimerState.Running
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

interface TimerManager {
    val eventState: StateFlow<TimerEvent>
    fun startTimer(timer: Timer)
    fun playPause()
    fun nextInterval()
    fun previousInterval()
    fun destroy()
    fun restartCurrentTimer()
    fun isRunning(): Boolean
}

internal class TimerManagerImpl(private val timerRepository: ITimerRepository) : TimerManager {
    private var timerStateMachine: TimerStateMachineImpl? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _eventState = MutableStateFlow<TimerEvent>(TimerEvent.Destroy())
    override val eventState: StateFlow<TimerEvent> = _eventState
    private var currentTimer: Timer? = null

    override fun startTimer(timer: Timer) {
        currentTimer = timer
        initTimerStateMachine(timer)
    }

    private fun initTimerStateMachine(timer: Timer) {
        val timerStateMachine = TimerStateMachineImpl(timer, coroutineScope)
        this.timerStateMachine = timerStateMachine
        coroutineScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                _eventState.emit(timerEvent)
                when (timerEvent) {
                    is TimerEvent.Finished -> updateTimerFinishedStats(timer)
                    is TimerEvent.Started -> updateTimerStartStats(timer)
                    else -> {}
                }
            }
        }
    }

    override fun restartCurrentTimer() {
        val currentTimer = currentTimer
        requireNotNull(currentTimer) {
            "Attempting to restart with null timer"
        }
        initTimerStateMachine(currentTimer)
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

    override fun playPause() {
        val timerStateMachine = timerStateMachine
        requireNotNull(timerStateMachine) {
            "Attempting to play/pause with null timer"
        }
        val timerState = timerStateMachine.eventState.value.runState.timerState
        when (timerState) {
            Running -> timerStateMachine.pause()
            Paused -> timerStateMachine.resume()
            Finished -> error("Cannot play/pause finished timer")
        }
    }

    override fun nextInterval() {
        timerStateMachine?.nextInterval()
    }

    override fun previousInterval() {
        timerStateMachine?.previousInterval()
    }

    override fun destroy() {
        timerStateMachine?.destroy()
        timerStateMachine = null
    }

    override fun isRunning() = timerStateMachine != null
}
