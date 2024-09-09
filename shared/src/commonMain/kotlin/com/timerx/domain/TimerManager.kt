package com.timerx.domain

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.vibration.IVibrationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class TimerManager(
    private val beepManager: IBeepManager,
    private val vibrationManager: IVibrationManager,
    private val notificationManager: ITimerXNotificationManager,
    private val timerXAnalytics: ITimerXAnalytics,
    private val timerRepository: ITimerRepository
) {
    private var timerStateMachine: TimerStateMachineImpl? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

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
                println("#### $timerEvent")
                _eventState.value = timerEvent
                when (timerEvent) {
                    is TimerEvent.Ticker -> {
                        timerXAnalytics.logEvent(
                            TICKER,
                            mapOf(Pair(ELAPSED, timerEvent.runState.elapsed))
                        )
                        timerEvent.beep?.let { beepManager.beep(it) }
                        timerEvent.vibration?.let { vibrationManager.vibrate(it) }
                    }

                    is TimerEvent.Finished -> {
                        beepManager.beep(timerEvent.beep)
                        vibrationManager.vibrate(timerEvent.vibration)
                        notificationManager.stop()
                        updateTimerFinishedStats(timer)
                    }

                    is TimerEvent.NextInterval -> {
                        beepManager.beep(timerEvent.beep)
                        vibrationManager.vibrate(timerEvent.vibration)
                    }

                    is TimerEvent.PreviousInterval -> {
                        beepManager.beep(timerEvent.beep)
                        vibrationManager.vibrate(timerEvent.vibration)
                    }

                    is TimerEvent.Started -> {
                        beepManager.beep(timerEvent.beep)
                        vibrationManager.vibrate(timerEvent.vibration)
                        notificationManager.start()
                    }

                    is TimerEvent.Resumed -> {
                        // Noop
                    }

                    is TimerEvent.Paused -> {
                        // noop
                    }

                    is TimerEvent.Destroy -> {
                        notificationManager.stop()
                        _eventState.value = TimerEvent.Idle
                    }

                    TimerEvent.Idle -> {

                    }
                }

                if (timerEvent.shouldNotify()) {
                    notificationManager.updateNotification(timerEvent)
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

    private fun TimerEvent.shouldNotify() =
        this !is TimerEvent.Destroy && this !is TimerEvent.Finished

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
        notificationManager.stop()
        timerStateMachine?.destroy()
        timerStateMachine = null
    }

    fun isRunning() = timerStateMachine != null

    companion object {
        private const val TICKER = "Ticker"
        private const val ELAPSED = "elapsed"
    }
}
