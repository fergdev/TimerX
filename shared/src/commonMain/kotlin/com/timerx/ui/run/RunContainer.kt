package com.timerx.ui.run

import com.timerx.analytics.TimerXAnalytics
import com.timerx.database.ITimerRepository
import com.timerx.settings.AlertSettings
import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import com.timerx.timermanager.TimerState
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
    private val timerXSettings: TimerXSettings,
    private val timerManager: TimerManager,
    timerRepository: ITimerRepository,
    timerXAnalytics: TimerXAnalytics,
) : Container<RunScreenState, RunScreenIntent, Nothing> {

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
            observeTimerPlugin(timerManager, timerXSettings),
            reducePlugin(timerManager, timerXSettings, timerRepository, timerId)
        )
    }
}

internal fun observeTimerPlugin(
    timerManager: TimerManager,
    timerXSettings: TimerXSettings,
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
    timerXSettings: TimerXSettings,
    timerRepository: ITimerRepository,
    timerId: Long
) =
    reducePlugin<RunScreenState, RunScreenIntent, Nothing> {
        when (it) {
            RunScreenIntent.NextInterval -> timerManager.nextInterval()
            RunScreenIntent.PreviousInterval -> timerManager.previousInterval()
            RunScreenIntent.OnManualNext -> timerManager.nextInterval()
            RunScreenIntent.Pause -> timerManager.playPause()
            RunScreenIntent.Play -> timerManager.playPause()
            RunScreenIntent.RestartTimer -> {
                val timer = timerRepository.getTimer(timerId).first()
                requireNotNull(timer) {
                    "Attempting to restart with null timer $timerId"
                }
                timerManager.startTimer(timer)
            }

            is RunScreenIntent.UpdateVibrationEnabled ->
                timerXSettings.alertSettingsManager.setVibrationEnabled(it.enabled)

            is RunScreenIntent.UpdateVolume -> timerXSettings.alertSettingsManager.setVolume(it.volume)
        }
    }
