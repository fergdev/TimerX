package com.intervallum.ui.run

import com.intervallum.analytics.IntervallumAnalytics
import com.intervallum.database.ITimerRepository
import com.intervallum.settings.AlertSettings
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.settings.IntervallumSettings
import com.intervallum.timermanager.TimerEvent
import com.intervallum.timermanager.TimerManager
import com.intervallum.timermanager.TimerState
import com.intervallum.ui.di.ConfigurationFactory
import com.intervallum.ui.di.configure
import com.intervallum.ui.run.RunScreenIntent.NextInterval
import com.intervallum.ui.run.RunScreenIntent.OnManualNext
import com.intervallum.ui.run.RunScreenIntent.Pause
import com.intervallum.ui.run.RunScreenIntent.Play
import com.intervallum.ui.run.RunScreenIntent.PreviousInterval
import com.intervallum.ui.run.RunScreenIntent.RestartTimer
import com.intervallum.ui.run.RunScreenIntent.UpdateVibrationEnabled
import com.intervallum.ui.run.RunScreenState.Loaded.Finished
import com.intervallum.ui.run.RunScreenState.Loaded.NotFinished.Paused
import com.intervallum.ui.run.RunScreenState.Loaded.NotFinished.Playing
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
    configurationFactory: ConfigurationFactory,
    private val alertSettingsManager: AlertSettingsManager,
    private val intervallumSettings: IntervallumSettings,
    private val timerManager: TimerManager,
    timerRepository: ITimerRepository,
    intervallumAnalytics: IntervallumAnalytics,
) : Container<RunScreenState, RunScreenIntent, RunAction> {

    override val store = store(RunScreenState.Loading) {
        configure(configurationFactory, "Run")
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
                intervallumAnalytics.logEvent("TimerStart")
            }
        }

        install(
            observeTimerPlugin(timerManager, alertSettingsManager, intervallumSettings),
            reducePlugin(
                timerManager,
                alertSettingsManager,
                timerRepository,
                timerId
            )
        )
    }
}

internal fun observeTimerPlugin(
    timerManager: TimerManager,
    alertSettingsManager: AlertSettingsManager,
    intervallumSettings: IntervallumSettings
) = plugin<RunScreenState, RunScreenIntent, RunAction> {
    onSubscribe {
        launch {
            combine(
                timerManager.eventState,
                alertSettingsManager.alertSettings,
                intervallumSettings.keepScreenOn
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
