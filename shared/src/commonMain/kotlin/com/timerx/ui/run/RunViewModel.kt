package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.RunState
import com.timerx.TimerEvent
import com.timerx.TimerStateMachineImpl
import com.timerx.beep.BeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.notification.TimerXNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

sealed interface RunScreenState {
    val backgroundColor: Color

    class Finished(override val backgroundColor: Color) : RunScreenState

    sealed class TimerInfo(
        override val backgroundColor: Color,
        val index: String? = "",
        val time: Int = 0,
        val name: String = "",
        val manualNext: Boolean = false,
    ) : RunScreenState

    class Running(
        backgroundColor: Color,
        index: String? = "",
        elapsed: Int = 0,
        name: String = "",
        manualNext: Boolean = false,
    ) : TimerInfo(backgroundColor, index, elapsed, name, manualNext)

    class Paused(
        backgroundColor: Color,
        index: String? = "",
        elapsed: Int = 0,
        name: String = "",
        manualNext: Boolean = false
    ) : TimerInfo(backgroundColor, index, elapsed, name, manualNext)
}

class RunViewModel(
    private val timerId: String,
    timerRepository: ITimerRepository,
    private val beepMaker: BeepMaker,
    private val notificationManager: TimerXNotificationManager,
) : ViewModel() {

    private val timer: Timer = timerRepository.getTimers().first { it.id == timerId }
    private val timerStateMachine = TimerStateMachineImpl(timer)

    private val _state = MutableStateFlow<RunScreenState>(
        RunScreenState.Running(
            backgroundColor = Color.Transparent
        )
    )

    val state: StateFlow<RunScreenState> = _state

    class Interactions(
        val play: () -> Unit,
        val pause: () -> Unit,
        val nextInterval: () -> Unit,
        val previousInterval: () -> Unit,
        val onManualNext: () -> Unit,
        val restartTimer: () -> Unit
    )

    val interactions = Interactions(
        play = timerStateMachine::start,
        pause = timerStateMachine::stop,
        nextInterval = timerStateMachine::nextInterval,
        previousInterval = timerStateMachine::previousInterval,
        onManualNext = ::onManualNext,
        restartTimer = ::initTimer
    )

    init {
        initTimer()
    }

    private fun initTimer() {
        viewModelScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                println(timerEvent::class.simpleName)
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
                            RunScreenState.Running(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index,
                                elapsed,
                                timerEvent.runState.intervalName
                            )
                        }
                        notificationManager.updateNotification(notificationState(timerEvent.runState))
                    }

                    is TimerEvent.Finished -> {
                        _state.value = RunScreenState.Finished(timerEvent.runState.backgroundColor)
                        beepMaker.beepFinished()
                        notificationManager.stop()
                    }

                    is TimerEvent.NextInterval -> {
                        _state.update {
                            RunScreenState.Running(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index,
                                elapsed,
                                timerEvent.runState.intervalName
                            )
                        }
                        beepMaker.beepNext()
                    }

                    is TimerEvent.PreviousInterval -> {
                        _state.update {
                            RunScreenState.Running(
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index,
                                elapsed,
                                timerEvent.runState.intervalName
                            )
                        }
                        beepMaker.beepPrevious()
                    }

                    is TimerEvent.Started -> {
                        _state.value = RunScreenState.Running(
                            backgroundColor = timerEvent.runState.backgroundColor,
                            index,
                            elapsed,
                            timerEvent.runState.intervalName
                        )

                        beepMaker.beepStarted()
                        notificationManager.start()
                    }

                    is TimerEvent.Stopped -> {
                        notificationManager.stop()
                        _state.value = RunScreenState.Paused(
                            backgroundColor = timerEvent.runState.backgroundColor,
                            index,
                            elapsed,
                            timerEvent.runState.intervalName
                        )
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

    override fun onCleared() {
        super.onCleared()
        timerStateMachine.destroy()
        notificationManager.stop()
    }
}
