package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.analytics.ITimerXAnalytics
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.domain.RunState
import com.timerx.domain.Timer
import com.timerx.domain.TimerEvent
import com.timerx.domain.TimerState
import com.timerx.domain.TimerStateMachineImpl
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.settings.TimerXSettings
import com.timerx.vibration.IVibrationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class RunScreenState(
    val timerState: TimerState = TimerState.Running,
    val backgroundColor: Color = Color.Transparent,
    val volume: Float,
    val vibrationEnabled: Boolean,
    val index: String? = "",
    val time: Int = 0,
    val name: String = "",
    val manualNext: Boolean = false,
    val timerName: String = ""
)

class RunViewModel(
    private val timerId: String,
    timerRepository: ITimerRepository,
    private val beepManager: IBeepManager,
    private val notificationManager: ITimerXNotificationManager,
    private val timerXSettings: TimerXSettings,
    private val timerXAnalytics: ITimerXAnalytics,
    private val vibrationManager: IVibrationManager
) : ViewModel() {

    private val timer: Timer = timerRepository.getTimers().first { it.id == timerId }
    private var timerStateMachine = TimerStateMachineImpl(timer)
    private val _state = MutableStateFlow(
        RunScreenState(
            volume = timerXSettings.volume,
            vibrationEnabled = timerXSettings.vibrationEnabled,
            timerName = timer.name.uppercase()
        )
    )

    val state: StateFlow<RunScreenState> = _state

    class Interactions(
        val play: () -> Unit,
        val pause: () -> Unit,
        val nextInterval: () -> Unit,
        val previousInterval: () -> Unit,
        val onManualNext: () -> Unit,
        val restartTimer: () -> Unit,
        val updateVolume: (Float) -> Unit,
        val updateVibrationEnabled: (Boolean) -> Unit
    )

    val interactions = Interactions(
        play = ::play,
        pause = timerStateMachine::pause,
        nextInterval = timerStateMachine::nextInterval,
        previousInterval = timerStateMachine::previousInterval,
        onManualNext = ::onManualNext,
        restartTimer = ::initTimer,
        updateVolume = ::updateVolume,
        updateVibrationEnabled = ::updateVibrationEnabled
    )

    init {
        initTimer()
        timerXAnalytics.logEvent("TimerStart", null)
    }

    private fun initTimer() {
        timerStateMachine.start()
        viewModelScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                updateRunState(timerEvent.runState)
                when (timerEvent) {
                    is TimerEvent.Ticker -> {
                        timerXAnalytics.logEvent(
                            "Ticker",
                            mapOf(Pair("elapsed", timerEvent.runState.elapsed))
                        )
                        notificationManager.updateNotification(notificationState(timerEvent.runState))
                        timerEvent.beep?.let { beepManager.beep(it) }
                        timerEvent.vibration?.let { vibrationManager.vibrate(it) }
                    }

                    is TimerEvent.Finished -> {
                        beepManager.beep(timerEvent.beep)
                        notificationManager.stop()
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
                        notificationManager.stop()
                    }
                }
            }
        }
    }

    private fun updateRunState(runState: RunState) {
        val elapsed = if (runState.displayCountAsUp) {
            runState.elapsed
        } else {
            runState.intervalDuration - runState.elapsed
        }
        val index = if (runState.setRepetitionCount != 1) {
            "${runState.setRepetitionCount - runState.repetitionIndex}"
        } else {
            null
        }
        _state.update {
            it.copy(
                timerState = runState.timerState,
                backgroundColor = runState.backgroundColor,
                index = index,
                time = elapsed,
                name = runState.intervalName,
                manualNext = runState.manualNext
            )
        }
    }

    private fun notificationState(runState: RunState): String {
        val set = runState.setIndex + 1
        val setCount = runState.setCount
        val interval = runState.intervalIndex + 1
        val intervalCount = runState.intervalCount
        val elapsed = runState.elapsed
        val intervalDuration = runState.intervalDuration
        return "($set / $setCount) - ($interval / $intervalCount) - ($elapsed / $intervalDuration)"
    }

    private fun onManualNext() {
        timerStateMachine.nextInterval()
    }

    private fun updateVolume(volume: Float) {
        timerXSettings.volume = volume
        _state.update { it.copy(volume = volume) }
    }

    private fun play() {
        if (timerStateMachine.eventState.value.runState.timerState == TimerState.Finished) {
            initTimer()
        } else {
            timerStateMachine.resume()
        }
    }

    private fun updateVibrationEnabled(enabled: Boolean) {
        timerXSettings.vibrationEnabled = enabled
        _state.update { it.copy(vibrationEnabled = enabled) }
    }

    override fun onCleared() {
        super.onCleared()
        timerStateMachine.destroy()
        notificationManager.stop()
    }
}