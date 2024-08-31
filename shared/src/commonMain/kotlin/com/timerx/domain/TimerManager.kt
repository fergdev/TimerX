package com.timerx.domain

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.beep.IBeepManager
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.vibration.IVibrationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerManager(
    private val beepManager: IBeepManager,
    private val vibrationManager: IVibrationManager,
    private val notificationManager: ITimerXNotificationManager,
    private val timerXAnalytics: ITimerXAnalytics
) {

    private var timerStateMachine: TimerStateMachineImpl? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val eventState: StateFlow<TimerEvent>
        get() = timerStateMachine!!.eventState

    fun startTimer(timer: Timer) {
        val timerStateMachine = TimerStateMachineImpl(timer, coroutineScope).apply {
            start()
        }
        this.timerStateMachine = timerStateMachine
        coroutineScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
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
                    }
                }

                if (timerEvent.shouldNotify()) {
                    notificationManager.updateNotification(timerEvent)
                }
            }
        }
    }

    private fun TimerEvent.shouldNotify(): Boolean {
        return this !is TimerEvent.Destroy && this !is TimerEvent.Finished
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
        notificationManager.stop()
        timerStateMachine?.destroy()
        timerStateMachine = null
    }

    fun isRunning(): Boolean {
        return timerStateMachine != null
    }

    companion object {
        private const val TICKER = "Ticker"
        private const val ELAPSED = "elapsed"
    }
}
