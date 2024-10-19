package com.timerx.ui.run

import com.timerx.analytics.TimerXAnalytics
import com.timerx.database.ITimerRepository
import com.timerx.platform.PlatformCapabilities
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
    private val platformCapabilities: PlatformCapabilities,
    timerRepository: ITimerRepository,
    timerXAnalytics: TimerXAnalytics,
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
            observeTimerPlugin(timerManager, timerXSettings, platformCapabilities),
            reducePlugin(timerManager, timerXSettings)
        )
    }
}

internal fun observeTimerPlugin(
    timerManager: TimerManager,
    timerXSettings: TimerXSettings,
    platformCapabilities: PlatformCapabilities
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
                                vibrationEnabled = settings.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext,
                                keepScreenOn = it.third,
                                canVibrate = platformCapabilities.canVibrate
                            )
                        }

                        TimerState.Paused -> {
                            Paused(
                                volume = settings.volume,
                                vibrationEnabled = settings.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                index = index,
                                time = elapsed,
                                intervalName = timerEvent.runState.intervalName,
                                manualNext = timerEvent.runState.manualNext,
                                keepScreenOn = it.third,
                                canVibrate = platformCapabilities.canVibrate
                            )
                        }

                        TimerState.Finished -> {
                            Finished(
                                volume = settings.volume,
                                vibrationEnabled = settings.vibrationEnabled,
                                timerName = timerEvent.runState.timerName,
                                backgroundColor = timerEvent.runState.backgroundColor,
                                keepScreenOn = it.third,
                                canVibrate = platformCapabilities.canVibrate
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
) =
    reducePlugin<RunScreenState, RunScreenIntent, Nothing> {
        when (it) {
            RunScreenIntent.NextInterval -> timerManager.nextInterval()
            RunScreenIntent.PreviousInterval -> timerManager.previousInterval()
            RunScreenIntent.OnManualNext -> timerManager.nextInterval()
            RunScreenIntent.Pause -> timerManager.playPause()
            RunScreenIntent.Play -> timerManager.playPause()
            RunScreenIntent.RestartTimer -> timerManager.restartCurrentTimer()
            is RunScreenIntent.UpdateVibrationEnabled ->
                timerXSettings.alertSettingsManager.setVibrationEnabled(it.enabled)
            is RunScreenIntent.UpdateVolume -> timerXSettings.alertSettingsManager.setVolume(it.volume)
        }
    }
