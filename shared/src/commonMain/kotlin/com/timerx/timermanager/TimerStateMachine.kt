package com.timerx.timermanager

import androidx.compose.ui.graphics.Color
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.sound.Beep
import com.timerx.sound.IntervalSound
import com.timerx.timermanager.TimerEvent.PreviousInterval
import com.timerx.timermanager.TimerEvent.Started
import com.timerx.util.ifTake
import com.timerx.vibration.Vibration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TimerEvent {
    val runState: RunState

    data object Idle : TimerEvent {
        override val runState = RunState(timerState = TimerState.Running)
    }

    data class Started(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class NextInterval(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class PreviousInterval(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class Finished(
        override val runState: RunState,
        val intervalSound: IntervalSound,
        val vibration: Vibration
    ) : TimerEvent

    data class Paused(override val runState: RunState) : TimerEvent
    data class Resumed(override val runState: RunState) : TimerEvent

    data class Ticker(
        override val runState: RunState,
        val beep: Beep?,
        val vibration: Vibration?
    ) : TimerEvent

    data class Destroy(override val runState: RunState) : TimerEvent
}

enum class TimerState {
    Running,
    Paused,
    Finished
}

data class RunState(
    val timerName: String = "",
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

    val elapsed: Long = 0,
    val intervalDuration: Long = 0,

    val backgroundColor: Color = Color.Transparent,
    val displayCountAsUp: Boolean = false,
    val manualNext: Boolean = false
)

interface TimerStateMachine {

    val eventState: StateFlow<TimerEvent>

    fun pause()

    fun nextInterval()

    fun previousInterval()

    fun destroy()

    fun resume()
}

class TimerStateMachineImpl(private val timer: Timer, private val coroutineScope: CoroutineScope) :
    TimerStateMachine {
    private val runState: MutableStateFlow<RunState> = MutableStateFlow(
        RunState(
            timerName = timer.name,
            intervalName = timer.sets[0].intervals[0].name
        )
    )
    private val _eventState: MutableStateFlow<TimerEvent>

    private var tickerJob: Job? = null

    override val eventState: StateFlow<TimerEvent>
        get() = _eventState

    init {
        updateRunState(0, 0, 0)
        with(getCurrentInterval()) {
            _eventState = MutableStateFlow(
                Started(
                    runState = runState.value,
                    intervalSound = intervalSound(),
                    vibration = vibration
                )
            )
        }
        startTicker()
    }

    override fun resume() {
        if (runState.value.timerState == TimerState.Finished) {
            return
        }
        runState.update { it.copy(timerState = TimerState.Running) }
        _eventState.value = TimerEvent.Resumed(runState.value)
        startTicker()
    }

    override fun pause() {
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
            updateRunState(
                setIndex,
                repetitionIndex,
                intervalIndex,
            )
            val currentInterval = getCurrentInterval()
            _eventState.value = TimerEvent.NextInterval(
                runState.value,
                currentInterval.intervalSound(),
                currentInterval.vibration
            )
        }
    }

    override fun previousInterval() {
        if (runState.value.timerState == TimerState.Finished) {
            return
        }

        if (runState.value.timerState == TimerState.Running) {
            restartTicker()
        }

        if (runState.value.elapsed != 0L) {
            runState.value = runState.value.copy(elapsed = 0)
            val currentInterval = getCurrentInterval()
            _eventState.value =
                PreviousInterval(
                    runState = runState.value,
                    intervalSound = currentInterval.intervalSound(),
                    vibration = currentInterval.vibration
                )
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

        updateRunState(
            setIndex = setIndex,
            repetitionIndex = repetitionIndex,
            intervalIndex = intervalIndex,
        )
        with(getCurrentInterval()) {
            _eventState.value = PreviousInterval(
                runState = runState.value,
                intervalSound = intervalSound(),
                vibration = vibration
            )
        }
    }

    override fun destroy() {
        _eventState.value = TimerEvent.Destroy(_eventState.value.runState)
        tickerJob?.cancel()
    }

    private fun startTicker() {
        tickerJob = coroutineScope.launch {
            while (true) {
                delay(TICKER_DELAY)
                val nextElapsed = runState.value.elapsed + 1
                runState.value = runState.value.copy(elapsed = nextElapsed)

                val currentInterval = getCurrentInterval()
                val timeLeft = currentInterval.duration - nextElapsed
                val beep =
                    if (timeLeft <= currentInterval.finalCountDown.duration && timeLeft != 0L) {
                        currentInterval.finalCountDown.beep
                    } else {
                        null
                    }
                val vibration =
                    if (timeLeft <= currentInterval.finalCountDown.duration && timeLeft != 0L) {
                        currentInterval.finalCountDown.vibration
                    } else {
                        null
                    }
                _eventState.value = TimerEvent.Ticker(runState.value, beep, vibration)
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

    private fun updateRunState(
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

    private fun getCurrentInterval() =
        timer.sets[runState.value.setIndex].intervals[runState.value.intervalIndex]

    private fun finishTimer() {
        runState.value = RunState(
            timerState = TimerState.Finished,
            backgroundColor = timer.finishColor
        )
        tickerJob?.cancel()
        _eventState.value =
            TimerEvent.Finished(
                runState.value,
                IntervalSound(timer.finishBeep, "Finished"),
                timer.finishVibration
            )
    }

    private fun TimerInterval.intervalSound() = IntervalSound(
        beep,
        textToSpeech.ifTake(name)
    )

    companion object {
        private const val TICKER_DELAY = 1000L
    }
}