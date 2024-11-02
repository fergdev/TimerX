package com.timerx.ui.run

import com.timerx.analytics.TimerXAnalytics
import com.timerx.database.ITimerRepository
import com.timerx.settings.AlertSettings
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import com.timerx.timermanager.TimerState
import com.timerx.ui.run.RunAction.Exit
import com.timerx.ui.run.RunScreenIntent.NextInterval
import com.timerx.ui.run.RunScreenIntent.OnManualNext
import com.timerx.ui.run.RunScreenIntent.Pause
import com.timerx.ui.run.RunScreenIntent.Play
import com.timerx.ui.run.RunScreenIntent.PreviousInterval
import com.timerx.ui.run.RunScreenIntent.RestartTimer
import com.timerx.ui.run.RunScreenIntent.UpdateVibrationEnabled
import com.timerx.ui.run.RunScreenState.Loaded.Finished
import com.timerx.ui.run.RunScreenState.Loaded.NotFinished.Paused
import com.timerx.ui.run.RunScreenState.Loaded.NotFinished.Playing
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
    private val alertSettingsManager: AlertSettingsManager,
    private val timerXSettings: TimerXSettings,
    private val timerManager: TimerManager,
    timerRepository: ITimerRepository,
    timerXAnalytics: TimerXAnalytics,
) : Container<RunScreenState, RunScreenIntent, RunAction> {

    override val store = store(RunScreenState.Loading) {
        init {
            launch {
                val timer = timerRepository.getTimer(timerId).first()
                // This could be from a pinned shortcut so nullability is not asserted here
                if (timer == null) {
                    updateState { RunScreenState.NoTimer }
                } else {
                    if (!timerManager.isRunning()) {
                        timerManager.startTimer(timer)
                    }
                }
                timerXAnalytics.logEvent("TimerStart")
            }
        }

        install(
            observeDestroyPlugin(timerManager),
            observeTimerPlugin(timerManager, alertSettingsManager, timerXSettings),
            reducePlugin(
                timerManager,
                alertSettingsManager,
                timerRepository,
                timerId
            )
        )
    }
}

internal fun observeDestroyPlugin(timerManager: TimerManager) =
    plugin<RunScreenState, RunScreenIntent, RunAction> {
        onSubscribe {
            launch {
                timerManager.eventState.collect {
                    if (it is TimerEvent.Destroy) {
                        action(Exit)
                    }
                }
            }
        }
    }

internal fun observeTimerPlugin(
    timerManager: TimerManager,
    alertSettingsManager: AlertSettingsManager,
    timerXSettings: TimerXSettings
) = plugin<RunScreenState, RunScreenIntent, RunAction> {
    onSubscribe {
        launch {
            combine(
                timerManager.eventState,
                alertSettingsManager.alertSettings,
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
                            Playing(
                                volume = settings.volume,
                                vibrationSetting = settings.vibrationSetting,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext,
                                keepScreenOn = it.third,
                            )
                        }

                        TimerState.Paused -> {
                            Paused(
                                volume = settings.volume,
                                vibrationSetting = settings.vibrationSetting,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext,
                                keepScreenOn = it.third,
                            )
                        }

                        TimerState.Finished -> {
                            Finished(
                                volume = settings.volume,
                                vibrationSetting = settings.vibrationSetting,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                keepScreenOn = it.third,
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
    alertSettingsManager: AlertSettingsManager,
    timerRepository: ITimerRepository,
    timerId: Long
) =
    reducePlugin<RunScreenState, RunScreenIntent, RunAction> {
        when (it) {
            NextInterval -> timerManager.nextInterval()
            PreviousInterval -> timerManager.previousInterval()
            OnManualNext -> timerManager.nextInterval()
            Pause -> timerManager.playPause()
            Play -> timerManager.playPause()
            RestartTimer -> {
                val timer = timerRepository.getTimer(timerId).first()
                requireNotNull(timer) { "Attempting to restart with null timer $timerId" }
                timerManager.startTimer(timer)
            }

            is UpdateVibrationEnabled ->
                alertSettingsManager.setVibrationEnabled(it.enabled)

            is RunScreenIntent.UpdateVolume -> alertSettingsManager.setVolume(it.volume)
        }
    }
