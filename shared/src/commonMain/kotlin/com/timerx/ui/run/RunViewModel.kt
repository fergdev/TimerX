package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.RunState
import com.timerx.TimerEvent
import com.timerx.TimerState
import com.timerx.TimerStateMachineImpl
import com.timerx.beep.BeepMaker
import com.timerx.beep.VolumeManager
import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.notification.TimerXNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class RunScreenState(
    val timerState: TimerState = TimerState.Running,
    val backgroundColor: Color,
    val volume: Float,
    val index: String? = "",
    val time: Int = 0,
    val name: String = "",
    val manualNext: Boolean = false,
)

class RunViewModel(
    private val timerId: String,
    timerRepository: ITimerRepository,
    private val beepMaker: BeepMaker,
    private val notificationManager: TimerXNotificationManager,
    private val volumeManager: VolumeManager
) : ViewModel() {

    private val timer: Timer = timerRepository.getTimers().first { it.id == timerId }
    private val timerStateMachine = TimerStateMachineImpl(timer)

    private val _state = MutableStateFlow(
        RunScreenState(
            backgroundColor = Color.Transparent,
            volume = volumeManager.getVolume()
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
        play = timerStateMachine::start,
        pause = timerStateMachine::stop,
        nextInterval = timerStateMachine::nextInterval,
        previousInterval = timerStateMachine::previousInterval,
        onManualNext = ::onManualNext,
        restartTimer = ::initTimer,
        updateVolume = ::updateVolume
    )

    init {
        initTimer()
        viewModelScope.launch {
            volumeManager.volumeFlow.collect { volume ->
                _state.update { it.copy(volume = volume) }
            }
        }
    }

    private fun initTimer() {
        viewModelScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                val elapsed = if (timerEvent.runState.displayCountAsUp) {
                    timerEvent.runState.elapsed
                } else {
                    timerEvent.runState.intervalDuration - timerEvent.runState.elapsed
                }
                val index = if (timerEvent.runState.repetitionIndex != 0) {
                    "${timerEvent.runState.repetitionIndex}"
                } else {
                    null
                }
                when (timerEvent) {
                    is TimerEvent.Ticker -> {
                        _state.update {
                            it.copy(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                name = timerEvent.runState.intervalName
                            )
                        }
                        notificationManager.updateNotification(notificationState(timerEvent.runState))
                    }

                    is TimerEvent.Finished -> {
                        _state.update {
                            it.copy(
                                timerState = TimerState.Finished,
                                backgroundColor = timerEvent.runState.backgroundColor,
                            )
                        }
                        beepMaker.beepFinished()
                        notificationManager.stop()
                    }

                    is TimerEvent.NextInterval -> {
                        _state.update {
                            it.copy(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                name = timerEvent.runState.intervalName
                            )
                        }
                        beepMaker.beepNext()
                    }

                    is TimerEvent.PreviousInterval -> {
                        _state.update {
                            it.copy(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                name = timerEvent.runState.intervalName
                            )
                        }
                        beepMaker.beepPrevious()
                    }

                    is TimerEvent.Started -> {
                        _state.update {
                            it.copy(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                timerState = TimerState.Running,
                                index = index,
                                time = elapsed,
                                name = timerEvent.runState.intervalName
                            )
                        }

                        beepMaker.beepStarted()
                        notificationManager.start()
                    }

                    is TimerEvent.Stopped -> {
                        notificationManager.stop()
                        _state.update {
                            it.copy(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                timerState = TimerState.Paused,
                                index = index,
                                time = elapsed,
                                name = timerEvent.runState.intervalName
                            )
                        }
                    }
                }
            }
        }
        timerStateMachine.start()
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
        volumeManager.setVolume(volume)
    }

    override fun onCleared() {
        super.onCleared()
        timerStateMachine.destroy()
        notificationManager.stop()
    }
}
