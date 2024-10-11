package com.timerx.timermanager

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.sound.ISoundManager
import com.timerx.vibration.IVibrationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class TimerManager(
    private val beepManager: ISoundManager,
    private val vibrationManager: IVibrationManager,
    private val timerRepository: ITimerRepository
) {
    private var timerStateMachine: TimerStateMachineImpl? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Unconfined)

    private val _eventState = MutableStateFlow<TimerEvent>(TimerEvent.Idle)
    val eventState: StateFlow<TimerEvent> = _eventState
    private var currentTimer: Timer? = null

    fun startTimer(timer: Timer) {
        currentTimer = timer
        initTimerStateMachine(timer)
    }

    private fun initTimerStateMachine(timer: Timer) {
        val timerStateMachine = TimerStateMachineImpl(timer, coroutineScope)
        updateTimerStartStats(timer)
        this.timerStateMachine = timerStateMachine
        coroutineScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                _eventState.value = timerEvent
                when (timerEvent) {
                    is TimerEvent.Ticker -> {
                        timerEvent.beep?.let { beepManager.beep(it) }
                        timerEvent.vibration?.let { vibrationManager.vibrate(it) }
                    }

                    is TimerEvent.Finished -> {
                        beepManager.makeIntervalSound(timerEvent.intervalSound)
                        vibrationManager.vibrate(timerEvent.vibration)
                        updateTimerFinishedStats(timer)
                    }

                    is TimerEvent.NextInterval -> {
                        beepManager.makeIntervalSound(timerEvent.intervalSound)
                        vibrationManager.vibrate(timerEvent.vibration)
                    }

                    is TimerEvent.PreviousInterval -> {
                        beepManager.makeIntervalSound(timerEvent.intervalSound)
                        vibrationManager.vibrate(timerEvent.vibration)
                    }

                    is TimerEvent.Started -> {
                        beepManager.makeIntervalSound(timerEvent.intervalSound)
                        vibrationManager.vibrate(timerEvent.vibration)
                    }

                    is TimerEvent.Resumed -> {
                        // Noop
                    }

                    is TimerEvent.Paused -> {
                        // noop
                    }

                    is TimerEvent.Destroy -> {
                        _eventState.value = TimerEvent.Idle
                    }

                    TimerEvent.Idle -> {
                    }
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
