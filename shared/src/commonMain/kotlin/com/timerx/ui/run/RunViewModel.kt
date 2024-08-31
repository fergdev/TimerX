package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.analytics.ITimerXAnalytics
import com.timerx.database.ITimerRepository
import com.timerx.domain.RunState
import com.timerx.domain.Timer
import com.timerx.domain.TimerEvent
import com.timerx.domain.TimerManager
import com.timerx.domain.TimerState
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
    val volume: Float = 1F,
    val vibrationEnabled: Boolean = true,
    val index: String? = "",
    val time: Int = 0,
    val name: String = "",
    val manualNext: Boolean = false,
    val timerName: String = "",
    val destroyed: Boolean = false
)

class RunViewModel(
    private val timerId: Long,
    timerRepository: ITimerRepository,
    timerXAnalytics: ITimerXAnalytics,
    private val timerXSettings: TimerXSettings,
    private val timerManager: TimerManager,
    private val timerDatabase: ITimerRepository
) : ViewModel() {

    private lateinit var timer: Timer
    private val _state = MutableStateFlow(RunScreenState())

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

    val interactions: Interactions = Interactions(
        play = ::play,
        pause = ::pause,
        nextInterval = ::nextInterval,
        previousInterval = ::previousInterval,
        onManualNext = ::onManualNext,
        restartTimer = ::initTimer,
        updateVolume = ::updateVolume,
        updateVibrationEnabled = ::updateVibrationEnabled
    )

    init {
        viewModelScope.launch {
            timerRepository.getTimer(timerId).collect {
                this@RunViewModel.timer = it
                if (timerManager.isRunning().not()) {
                    initTimer()
                }
            }
        }
        timerXAnalytics.logEvent("TimerStart", null)
        viewModelScope.launch {
            timerXSettings.settings.collect { settings ->
                _state.update {
                    it.copy(
                        volume = settings.volume,
                        vibrationEnabled = settings.vibrationEnabled
                    )
                }
            }
        }
    }

    private fun previousInterval() {
        timerManager.previousInterval()
    }

    private fun initTimer() {
        timerManager.startTimer(timer)
        viewModelScope.launch {
            timerManager.eventState.collect { timerEvent ->
                updateRunState(timerEvent.runState)
                if (timerEvent is TimerEvent.Destroy) {
                    _state.update { it.copy(destroyed = true) }
                } else if (timerEvent is TimerEvent.Finished) {
                    timerDatabase.updateTimerStats(
                        timer.id,
                        timer.startedCount,
                        timer.completedCount + 1
                    )
                }
            }
        }
        viewModelScope.launch {
            timerDatabase.updateTimerStats(
                timer.id,
                timer.startedCount + 1,
                timer.completedCount
            )
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
                timerName = timer.name,
                timerState = runState.timerState,
                backgroundColor = runState.backgroundColor,
                index = index,
                time = elapsed,
                name = runState.intervalName,
                manualNext = runState.manualNext
            )
        }
    }

    private fun onManualNext() {
        timerManager.nextInterval()
    }

    private fun updateVolume(volume: Float) {
        viewModelScope.launch {
            timerXSettings.setVolume(volume)
        }
    }

    private fun play() {
        if (timerManager.eventState.value.runState.timerState == TimerState.Finished) {
            initTimer()
        } else {
            timerManager.playPause()
        }
    }

    private fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            timerXSettings.setVibrationEnabled(enabled)
        }
    }

    private fun pause() {
        timerManager.playPause()
    }

    private fun nextInterval() {
        timerManager.nextInterval()
    }

    override fun onCleared() {
        super.onCleared()
        timerManager.destroy()
    }
}