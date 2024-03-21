package com.timerx.ui.run

import com.timerx.beep.BeepMaker
import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.ui.run.RunViewModel.TimerState.Finished
import com.timerx.ui.run.RunViewModel.TimerState.Paused
import com.timerx.ui.run.RunViewModel.TimerState.Running
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class RunViewModel(
    private val timerId: String,
    timerRepository: ITimerRepository,
    private val beepMaker: BeepMaker,
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

        val setRepetition: Long = 0,
        val setRepetitionCount: Long = 0,

        val interval: Int = 0,

        val intervalRepetition: Int = 0,
        val intervalRepetitionCount: Int = 0,

        val intervalCount: Int = 0,
        val intervalName: String = "",

        val elapsed: Long = 0,
        val intervalDuration: Long = 0
    )

    private val timer: Timer = timerRepository.getTimers().first { it.id == timerId }

    private val _state = MutableStateFlow(RunState())

    val state: StateFlow<RunState> = _state

    private var tickerJob: Job? = null

    init {
        initTimer()
    }

    fun toggleState() {
        when (state.value.timerState) {
            Running -> {
                _state.value = _state.value.copy(timerState = Paused)
                stopTicker()
            }

            Paused -> {
                _state.value = _state.value.copy(timerState = Running)
                startTicker()
            }

            Finished -> {
                // Nothing
            }
        }
    }

    private fun updateState(
        set: Int,
        repetition: Long,
        interval: Int,
    ) {
        val repetitionCount = timer.sets[set].repetitions
        val intervalCount = timer.sets[set].intervals.size
        val intervalName = timer.sets[set].intervals[interval].name
        val intervalDuration = timer.sets[set].intervals[interval].duration

        _state.value = _state.value.copy(
            set = set,

            setRepetition = repetition,
            setRepetitionCount = repetitionCount,

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
        var repetitionIndex = _state.value.setRepetition
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
            stopTicker()
        } else {
            restartTicker()
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

        restartTicker()
        beepMaker.beepBack()

        if (_state.value.elapsed != 0L) {
            _state.value = _state.value.copy(elapsed = 0)
            return
        }

        var setIndex = _state.value.set
        var repetitionIndex = _state.value.setRepetition
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

    private fun restartTicker() {
        stopTicker()
        startTicker()
    }

    private fun stopTicker() {
        tickerJob?.cancel()
    }

    private fun startTicker() {
        tickerJob = viewModelScope.launch {
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
            setRepetitionCount = firstSet.repetitions,

            intervalCount = firstSet.intervals.size,

            intervalName = firstInterval.name,
            intervalDuration = firstInterval.duration
        )
        beepMaker.beepStart()
        startTicker()
    }

    override fun onCleared() {
        super.onCleared()
        stopTicker()
    }
}