package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import com.timerx.analytics.ITimerXAnalytics
import com.timerx.database.ITimerRepository
import com.timerx.domain.TimerManager
import com.timerx.domain.TimerState
import com.timerx.settings.ITimerXSettings
import com.timerx.ui.run.RunScreenState.Finished
import com.timerx.ui.run.RunScreenState.NotFinished.Paused
import com.timerx.ui.run.RunScreenState.NotFinished.Playing
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.plugin
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.reducePlugin

internal class RunContainer(
    private val timerId: Long,
    private val timerXSettings: ITimerXSettings,
    private val timerManager: TimerManager,
    timerRepository: ITimerRepository,
    timerXAnalytics: ITimerXAnalytics,
) : Container<RunScreenState, RunScreenIntent, Nothing> {

    override val store = store(
        Playing(
            backgroundColor = Color.Transparent,
            volume = 0F,
            vibrationEnabled = true,
            index = "",
            time = 0,
            intervalName = "",
            manualNext = false,
            timerName = ""
        )
    ) {
        init {
            launch {
                val timer = timerRepository.getTimer(timerId).first()
                if (timerManager.isRunning().not()) {
                    timerManager.startTimer(timer)
                }
            }
            timerXAnalytics.logEvent("TimerStart")
        }

        install(
            updateSettingsPlugin(timerXSettings),
            observeTimerPlugin(timerManager),
            reducePlugin(timerManager, timerXSettings)
        )
    }
}

internal fun updateSettingsPlugin(timerXSettings: ITimerXSettings) =
    plugin<RunScreenState, RunScreenIntent, Nothing> {
        onSubscribe {
            launch {
                timerXSettings.settings.collect { settings ->
                    updateState {
                        when (this) {
                            is Finished -> {
                                copy(
                                    volume = settings.volume,
                                    vibrationEnabled = settings.vibrationEnabled
                                )
                            }

                            is Paused -> {
                                copy(
                                    volume = settings.volume,
                                    vibrationEnabled = settings.vibrationEnabled
                                )
                            }

                            is Playing -> {
                                copy(
                                    volume = settings.volume,
                                    vibrationEnabled = settings.vibrationEnabled
                                )
                            }
                        }
                    }
                }
            }
        }
    }

internal fun observeTimerPlugin(
    timerManager: TimerManager
) = plugin<RunScreenState, RunScreenIntent, Nothing> {
    onSubscribe {
        launch {
            timerManager.eventState.collect { timerEvent ->
                val elapsed = if (timerEvent.runState.displayCountAsUp) {
                    timerEvent.runState.elapsed
                } else {
                    timerEvent.runState.intervalDuration - timerEvent.runState.elapsed
                }
                val index = if (timerEvent.runState.setRepetitionCount != 1) {
                    "${timerEvent.runState.setRepetitionCount - timerEvent.runState.repetitionIndex}"
                } else {
                    ""
                }
                updateState {
                    when (timerEvent.runState.timerState) {
                        TimerState.Running -> {
                            Playing(
                                volume = this.volume,
                                vibrationEnabled = this.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext
                            )
                        }

                        TimerState.Paused -> {
                            Paused(
                                volume = this.volume,
                                vibrationEnabled = this.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext
                            )
                        }

                        TimerState.Finished -> {
                            Finished(
                                volume = this.volume,
                                vibrationEnabled = this.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                            )
                        }
                    }
                }
            }
        }
    }

    onStop {
        timerManager.destroy()
    }
}

internal fun reducePlugin(
    timerManager: TimerManager,
    timerXSettings: ITimerXSettings,
) =
    reducePlugin<RunScreenState, RunScreenIntent, Nothing> {
        when (it) {
            RunScreenIntent.NextInterval -> timerManager.nextInterval()
            RunScreenIntent.PreviousInterval -> timerManager.previousInterval()
            RunScreenIntent.OnManualNext -> timerManager.nextInterval()
            RunScreenIntent.Pause -> timerManager.playPause()
            RunScreenIntent.Play -> {
                if (timerManager.eventState.value.runState.timerState == TimerState.Finished) {
                    timerManager.restartCurrentTimer()
                } else {
                    timerManager.playPause()
                }
            }

            RunScreenIntent.RestartTimer -> {
                timerManager.restartCurrentTimer()
            }

            is RunScreenIntent.UpdateVibrationEnabled -> timerXSettings.setVibrationEnabled(true)
            is RunScreenIntent.UpdateVolume -> timerXSettings.setVolume(it.volume)
        }
    }
