package com.timerx.ui.run

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.database.ITimerRepository
import com.timerx.domain.TimerEvent
import com.timerx.domain.TimerManager
import com.timerx.domain.TimerState
import com.timerx.settings.AlertSettings
import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.flow.combine
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

    override val store = store(RunScreenState.Loading) {
        init {
            launch {
                val timer = timerRepository.getTimer(timerId).first()
                if (timer == null) {
                    updateState { RunScreenState.NoTimer }
                } else {
                    if (timerManager.isRunning().not()) {
                        timerManager.startTimer(timer)
                    }
                }
                timerXAnalytics.logEvent("TimerStart")
            }
        }

        install(
            observeTimerPlugin(timerManager, timerXSettings),
            reducePlugin(timerManager, timerXSettings)
        )
    }
}

internal fun observeTimerPlugin(
    timerManager: TimerManager,
    timerXSettings: ITimerXSettings
) = plugin<RunScreenState, RunScreenIntent, Nothing> {
    onSubscribe {
        launch {
            combine(
                timerManager.eventState,
                timerXSettings.alertSettingsManager.alertSettings,
                timerXSettings.keepScreenOn
            ) { timerEvent: TimerEvent, alertSettings: AlertSettings, keepScreenOn ->
                Triple(timerEvent, alertSettings, keepScreenOn)
            }.collect {
                val timerEvent = it.first
                val settings = it.second
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
                            RunScreenState.Loaded.NotFinished.Playing(
                                volume = settings.volume,
                                vibrationEnabled = settings.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext,
                                keepScreenOn = it.third
                            )
                        }

                        TimerState.Paused -> {
                            RunScreenState.Loaded.NotFinished.Paused(
                                volume = settings.volume,
                                vibrationEnabled = settings.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext,
                                keepScreenOn = it.third
                            )
                        }

                        TimerState.Finished -> {
                            RunScreenState.Loaded.Finished(
                                volume = settings.volume,
                                vibrationEnabled = settings.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                keepScreenOn = it.third
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
            RunScreenIntent.Play -> timerManager.playPause()
            RunScreenIntent.RestartTimer -> timerManager.restartCurrentTimer()
            is RunScreenIntent.UpdateVibrationEnabled -> timerXSettings.alertSettingsManager.setVibrationEnabled(
                it.enabled
            )

            is RunScreenIntent.UpdateVolume -> timerXSettings.alertSettingsManager.setVolume(it.volume)
        }
    }
