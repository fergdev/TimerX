package com.timerx.android.run

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timerx.android.beep.BeepMaker
import com.timerx.android.beep.BeepMakerImpl
import com.timerx.android.main.Screens.RUN_TIMER_ID
import com.timerx.android.run.RunViewModel.TimerState.Finished
import com.timerx.android.run.RunViewModel.TimerState.Paused
import com.timerx.android.run.RunViewModel.TimerState.Running
import com.timerx.domain.Timer
import com.timerx.repository.TimerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RunViewModel(
    timerRepository: TimerRepository,
    savedStateHandle: SavedStateHandle,
    private val beepMaker: BeepMaker = BeepMakerImpl(),
) : ViewModel() {

    enum class TimerState {
        Running,
        Paused,
        Finished
    }

    data class RunState(
        val timerState: TimerState = Running,
        val timerName: String = "",

        val set: Int = 0,
        val setCount: Int = 0,

        val repetition: Int = 0,
        val repetitionCount: Int = 0,

        val interval: Int = 0,
        val intervalCount: Int = 0,
        val intervalName: String = "",

        val elapsed: Int = 0,
        val intervalDuration: Int = 0
    )

    private val timerId: Int = savedStateHandle[RUN_TIMER_ID]!!
    private val timer: Timer = timerRepository.timers().first { it.id == timerId }

    private val _state = MutableStateFlow(RunState())

    val state: StateFlow<RunState> = _state

    private var timerJob: Job? = null

    init {
        initTimer()
    }

    fun toggleState() {
        when (state.value.timerState) {
            Running -> {
                _state.value = _state.value.copy(timerState = Paused)
                stopTimer()
            }

            Paused -> {
                _state.value = _state.value.copy(timerState = Running)
                startTimer()
            }

            Finished -> {
                // Nothing
            }
        }
    }

    private fun updateState(
        set: Int,
        repetition: Int,
        interval: Int,
    ) {
        val repetitionCount = timer.sets[set].repetitions
        val intervalCount = timer.sets[set].intervals.size
        val intervalName = timer.sets[set].intervals[interval].name
        val intervalDuration = timer.sets[set].intervals[interval].duration

        _state.value = _state.value.copy(
            set = set,

            repetition = repetition,
            repetitionCount = repetitionCount,

            interval = interval,
            intervalCount = intervalCount,
            intervalName = intervalName,

            elapsed = 0,
            intervalDuration = intervalDuration
        )
    }

    fun nextInterval() {
        if (state.value.timerState == Finished) {
            return
        }
        var setIndex = _state.value.set
        var repetitionIndex = _state.value.repetition
        var intervalIndex = _state.value.interval

        val timerSet = timer.sets[setIndex]

        if (++intervalIndex == timerSet.intervals.size) {
            intervalIndex = 0
            repetitionIndex++
        }

        if (repetitionIndex == timer.sets[setIndex].repetitions) {
            repetitionIndex = 0
            setIndex++
        }

        if (setIndex == timer.sets.size) {
            _state.value = RunState(timerState = Finished)
            beepMaker.beepFinished()
            stopTimer()
        } else {
            restartTimer()
            beepMaker.beepNext()
            updateState(
                setIndex,
                repetitionIndex,
                intervalIndex,
            )
        }
    }

    fun previousInterval() {
        if (state.value.timerState == Finished) {
            return
        }

        restartTimer()
        beepMaker.beepBack()

        if (_state.value.elapsed != 0) {
            _state.value = _state.value.copy(elapsed = 0)
            return
        }

        var setIndex = _state.value.set
        var repetitionIndex = _state.value.repetition
        var intervalIndex = _state.value.interval

        if (--intervalIndex < 0) {
            intervalIndex = 0
            repetitionIndex--
        }
        if (repetitionIndex < 0) {
            repetitionIndex = 0
            setIndex--
        }
        if (setIndex < 0) setIndex = 0

        updateState(
            setIndex,
            repetitionIndex,
            intervalIndex,
        )
    }

    private fun restartTimer() {
        stopTimer()
        startTimer()
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val nextElapsed = _state.value.elapsed + 1
                if (nextElapsed == _state.value.intervalDuration) {
                    nextInterval()
                } else {
                    _state.value = _state.value.copy(
                        elapsed = nextElapsed
                    )
                }
            }
        }
    }

    fun initTimer() {
        val firstSet = timer.sets.first()
        val firstInterval = firstSet.intervals.first()

        _state.value = _state.value.copy(
            timerState = Running,
            timerName = timer.name,

            setCount = timer.sets.size,
            repetitionCount = firstSet.repetitions,

            intervalCount = firstSet.intervals.size,

            intervalName = firstInterval.name,
            intervalDuration = firstInterval.duration
        )
        beepMaker.beepStart()
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}