package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.TimerXNotificationManager
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
    private val notificationManager: TimerXNotificationManager,
) : ViewModel() {

    enum class TimerState {
        Running,
        Paused,
        Finished
    }

    data class RunState(
        val timerState: TimerState = Running,
        val timerName: String = "",

        val setIndex: Int = 0,
        val setCount: Int = 0,

        val repetitionIndex: Int = 0,
        val setRepetitionCount: Int = 0,

        val intervalIndex: Int = 0,

        val intervalRepetition: Int = 0,
        val intervalRepetitionCount: Int = 0,

        val intervalCount: Int = 0,
        val intervalName: String = "",

        val elapsed: Int = 0,
        val intervalDuration: Int = 0,

        val backgroundColor: Color = Color.Red,
        val displayCountAsUp: Boolean = false,
        val manualNext: Boolean = false
    )

    private val timer: Timer = timerRepository.getTimers().first { it.id == timerId }

    private val _state = MutableStateFlow(RunState())

    val state: StateFlow<RunState> = _state

    class Interactions(
        val togglePlayState: () -> Unit,
        val nextInterval: () -> Unit,
        val previousInterval: () -> Unit,
        val onManualNext: () -> Unit,
        val restartTimer: () -> Unit
    )

    val interactions = Interactions(
        togglePlayState = ::togglePlayState,
        nextInterval = ::nextInterval,
        previousInterval = ::previousInterval,
        onManualNext = ::onManualNext,
        restartTimer = ::initTimer
    )

    private var tickerJob: Job? = null

    init {
        initTimer()
    }

    private fun togglePlayState() {
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
        setIndex: Int,
        repetitionIndex: Int,
        intervalIndex: Int,
    ) {
        val repetitionCount = timer.sets[setIndex].repetitions
        val intervalCount = timer.sets[setIndex].intervals.size

        val interval = timer.sets[setIndex].intervals[intervalIndex]
        val intervalName = interval.name
        val intervalDuration = interval.duration
        val color = interval.color
        val displayCountAsUp = interval.countUp

        _state.value = _state.value.copy(
            setIndex = setIndex,

            repetitionIndex = repetitionIndex,
            setRepetitionCount = repetitionCount,

            intervalIndex = intervalIndex,
            intervalCount = intervalCount,
            intervalName = intervalName,

            elapsed = 0,
            intervalDuration = intervalDuration,

            backgroundColor = color,
            displayCountAsUp = displayCountAsUp,

            manualNext = interval.manualNext
        )
    }

    private fun nextInterval() {
        if (state.value.timerState == Finished) {
            return
        }
        var setIndex = _state.value.setIndex
        var repetitionIndex = _state.value.repetitionIndex
        var intervalIndex = _state.value.intervalIndex

        val currentSet = timer.sets[setIndex]

        // Check for end of current repetition
        if (++intervalIndex == currentSet.intervals.size) {
            intervalIndex = 0
            repetitionIndex++
        }

        // Handle all skip on last set
        if (repetitionIndex == currentSet.repetitions - 1) {
            while (
                intervalIndex < currentSet.intervals.size &&
                currentSet.intervals[intervalIndex].skipOnLastSet
            ) {
                intervalIndex++
            }

            if (intervalIndex == currentSet.intervals.size) {
                intervalIndex = 0
                repetitionIndex++
            }
        }

        if (repetitionIndex == currentSet.repetitions) {
            repetitionIndex = 0
            setIndex++
        }

        if (setIndex == timer.sets.size) {
            finishTimer()
        } else {
            if (state.value.timerState == Running) {
                restartTicker()
            }
            beepMaker.beepNext()
            updateState(
                setIndex,
                repetitionIndex,
                intervalIndex,
            )
        }
    }

    private fun finishTimer() {
        _state.value = RunState(
            timerState = Finished,
            backgroundColor = timer.finishColor
        )
        beepMaker.beepFinished()
        stopTicker()
    }

    private fun previousInterval() {
        if (state.value.timerState == Finished) {
            return
        }

        if (state.value.timerState == Running) {
            restartTicker()
        }
        beepMaker.beepBack()

        if (_state.value.elapsed != 0) {
            _state.value = _state.value.copy(elapsed = 0)
            return
        }

        var setIndex = _state.value.setIndex
        var repetitionIndex = _state.value.repetitionIndex
        var intervalIndex = _state.value.intervalIndex

        if (setIndex == 0 && repetitionIndex == 0 && intervalIndex == 0) {
            return
        }

        if (--intervalIndex < 0) {
            repetitionIndex--

            if (repetitionIndex < 0) {
                setIndex--
                repetitionIndex = timer.sets[setIndex].repetitions - 1
            }
            intervalIndex = timer.sets[setIndex].intervals.size - 1
        }

        if (repetitionIndex == timer.sets[setIndex].repetitions - 1) {
            // Handle skip on last set
            while (
                intervalIndex >= 0 &&
                timer.sets[setIndex].intervals[intervalIndex].skipOnLastSet
            ) {
                intervalIndex--
            }

            if (intervalIndex < 0) {
                repetitionIndex--
                intervalIndex = timer.sets[setIndex].intervals.size - 1
            }
        }

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
                delay(TICKER_DELAY)
                val nextElapsed = _state.value.elapsed + 1
                _state.value = _state.value.copy(elapsed = nextElapsed)
                notificationManager.updateNotification(notificationState())
                if (nextElapsed == _state.value.intervalDuration) {
                    if (state.value.manualNext) {
                        break
                    } else {
                        nextInterval()
                    }
                }
            }
        }
    }

    private fun notificationState(): String {
        val set = state.value.setIndex + 1
        val setCount = state.value.setCount
        val interval = state.value.intervalIndex + 1
        val intervalCount = state.value.intervalCount
        val elapsed = state.value.elapsed
        val intervalDuration = state.value.intervalDuration
        return "($set / $setCount) - ($interval / $intervalCount) - ($elapsed / $intervalDuration)"
    }

    private fun initTimer() {
        val firstSet = timer.sets.first()
        val firstInterval = firstSet.intervals.first()

        _state.value = _state.value.copy(
            timerState = Running,
            timerName = timer.name,

            setCount = timer.sets.size,
            setRepetitionCount = firstSet.repetitions,

            intervalCount = firstSet.intervals.size,

            intervalName = firstInterval.name,
            intervalDuration = firstInterval.duration,

            backgroundColor = firstInterval.color,
            displayCountAsUp = firstInterval.countUp,
            manualNext = firstInterval.manualNext
        )
        notificationManager.startService()
        beepMaker.beepStart()
        startTicker()
    }

    private fun onManualNext() {
        nextInterval()
    }

    override fun onCleared() {
        super.onCleared()
        stopTicker()
        notificationManager.stopService()
    }

    companion object {
        private const val TICKER_DELAY = 1000L
    }
}
