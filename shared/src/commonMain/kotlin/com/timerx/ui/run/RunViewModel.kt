package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.beep.BeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.domain.RunState
import com.timerx.domain.Timer
import com.timerx.domain.TimerEvent
import com.timerx.domain.TimerState
import com.timerx.domain.TimerStateMachineImpl
import com.timerx.notification.TimerXNotificationManager
import com.timerx.settings.TimerXSettings
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
    val index: String? = "",
    val time: Int = 0,
    val name: String = "",
    val manualNext: Boolean = false,
    val timerName: String = ""
)

class RunViewModel(
    private val timerId: String,
    timerRepository: ITimerRepository,
    private val beepMaker: BeepMaker,
    private val notificationManager: TimerXNotificationManager,
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    private val timer: Timer = timerRepository.getTimers().first { it.id == timerId }
    private var timerStateMachine = TimerStateMachineImpl(timer)
    private val _state = MutableStateFlow(
        RunScreenState(
            volume = timerXSettings.volume,
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
        val updateVolume: (Float) -> Unit
    )

    val interactions = Interactions(
        play = ::play,
        pause = timerStateMachine::pause,
        nextInterval = timerStateMachine::nextInterval,
        previousInterval = timerStateMachine::previousInterval,
        onManualNext = ::onManualNext,
        restartTimer = ::initTimer,
        updateVolume = ::updateVolume
    )

    init {
        initTimer()
    }

    private fun initTimer() {
        timerStateMachine.start()
        viewModelScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                updateRunState(timerEvent.runState)
                when (timerEvent) {
                    is TimerEvent.Ticker -> {
                        notificationManager.updateNotification(notificationState(timerEvent.runState))
                    }

                    is TimerEvent.Finished -> {
                        beepMaker.beep(timerEvent.beep)
                        notificationManager.stop()
                    }

                    is TimerEvent.NextInterval -> {
                        beepMaker.beep(timerEvent.beep)
                    }

                    is TimerEvent.PreviousInterval -> {
                        beepMaker.beep(timerEvent.beep)
                    }

                    is TimerEvent.Started -> {
                        beepMaker.beep(timerEvent.beep)
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
                name = runState.intervalName
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

    override fun onCleared() {
        super.onCleared()
        timerStateMachine.destroy()
        notificationManager.stop()
    }
}