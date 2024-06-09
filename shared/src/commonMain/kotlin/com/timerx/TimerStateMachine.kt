package com.timerx

import androidx.compose.ui.graphics.Color
import com.timerx.domain.Timer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TimerEvent(val runState: RunState) {
    class Started(runState: RunState) : TimerEvent(runState)
    class Paused(runState: RunState) : TimerEvent(runState)
    class NextInterval(runState: RunState) : TimerEvent(runState)
    class PreviousInterval(runState: RunState) : TimerEvent(runState)
    class Finished(runState: RunState) : TimerEvent(runState)
    class Ticker(runState: RunState) : TimerEvent(runState)
}

enum class TimerState {
    Running,
    Paused,
    Finished
}

data class RunState(
    val timerState: TimerState = TimerState.Running,

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

interface TimerStateMachine {

    val eventState: StateFlow<TimerEvent>

    fun start()

    fun stop()

    fun nextInterval()

    fun previousInterval()

    fun destroy()

}

class TimerStateMachineImpl(private val timer: Timer) : TimerStateMachine {
    private val runState = MutableStateFlow(RunState())
    private val _eventState = MutableStateFlow<TimerEvent>(TimerEvent.Started(runState.value))
    private var tickerJob: Job? = null

    override val eventState: StateFlow<TimerEvent>
        get() = _eventState

    override fun start() {
        updateState(0, 0, 0)
        _eventState.value = TimerEvent.Started(runState.value)
        startTicker()
    }

    override fun stop() {
        tickerJob?.cancel()
        runState.value = runState.value.copy(timerState = TimerState.Paused)
        _eventState.value = TimerEvent.Paused(runState.value)
    }

    override fun nextInterval() {
        if (runState.value.timerState == TimerState.Finished) {
            return
        }
        var setIndex = runState.value.setIndex
        var repetitionIndex = runState.value.repetitionIndex
        var intervalIndex = runState.value.intervalIndex

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
            if (runState.value.timerState == TimerState.Running) {
                restartTicker()
            }
            updateState(
                setIndex,
                repetitionIndex,
                intervalIndex,
            )
            _eventState.value = TimerEvent.NextInterval(runState.value)
        }
    }

    override fun previousInterval() {
        if (runState.value.timerState == TimerState.Finished) {
            return
        }

        if (runState.value.timerState == TimerState.Running) {
            restartTicker()
        }

        if (runState.value.elapsed != 0) {
            runState.value = runState.value.copy(elapsed = 0)
            _eventState.value = TimerEvent.PreviousInterval(runState.value)
            return
        }

        var setIndex = runState.value.setIndex
        var repetitionIndex = runState.value.repetitionIndex
        var intervalIndex = runState.value.intervalIndex

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
        _eventState.value = TimerEvent.PreviousInterval(runState.value)
    }

    override fun destroy() {
        tickerJob?.cancel()
    }

    private fun startTicker() {
        tickerJob = GlobalScope.launch {
            while (true) {
                delay(TICKER_DELAY)
                val nextElapsed = runState.value.elapsed + 1
                runState.value = runState.value.copy(elapsed = nextElapsed)
                _eventState.value = TimerEvent.Ticker(runState.value)
                if (nextElapsed == runState.value.intervalDuration) {
                    if (runState.value.manualNext) {
                        break
                    } else {
                        nextInterval()
                    }
                }
            }
        }
    }

    private fun restartTicker() {
        tickerJob?.cancel()
        startTicker()
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

        runState.value = runState.value.copy(
            timerState = TimerState.Running,

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

    private fun finishTimer() {
        runState.value = RunState(
            timerState = TimerState.Finished,
            backgroundColor = timer.finishColor
        )
        tickerJob?.cancel()
        _eventState.value = TimerEvent.Finished(runState.value)
    }

    companion object {
        private const val TICKER_DELAY = 1000L
    }
}