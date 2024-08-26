package com.timerx.domain

import androidx.compose.ui.graphics.toArgb
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
        val timerStateMachine = TimerStateMachineImpl(timer, coroutineScope)
        this.timerStateMachine = timerStateMachine
        timerStateMachine.start()
        coroutineScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                when (timerEvent) {
                    is TimerEvent.Ticker -> {
                        timerXAnalytics.logEvent(
                            TICKER,
                            mapOf(Pair(ELAPSED, timerEvent.runState.elapsed))
                        )
                        notificationManager.updateNotification(
                            true,
                            notificationState(timerEvent.runState),
                            timerEvent.runState.backgroundColor.toArgb()
                        )
                        timerEvent.beep?.let { beepManager.beep(it) }
                        timerEvent.vibration?.let { vibrationManager.vibrate(it) }
                    }

                    is TimerEvent.Finished -> {
                        beepManager.beep(timerEvent.beep)
                        vibrationManager.vibrate(timerEvent.vibration)
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
                        notificationManager.start()
                    }

                    is TimerEvent.Paused -> {
                        notificationManager.updateNotification(
                            false,
                            notificationState(timerEvent.runState),
                            timerEvent.runState.backgroundColor.toArgb()
                        )
                    }

                    is TimerEvent.Destroy -> {
                        notificationManager.stop()
                    }
                }
            }
        }
    }

    private fun notificationState(runState: RunState): String {
        return "${runState.intervalName} - ${runState.intervalDuration - runState.elapsed}"
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

    companion object {
        private const val TICKER = "Ticker"
        private const val ELAPSED = "elapsed"
    }
}