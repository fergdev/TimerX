package com.intervallum.ui.main

import com.intervallum.database.ITimerRepository
import com.intervallum.permissions.IPermissionsHandler
import com.intervallum.permissions.Permission
import com.intervallum.permissions.PermissionState
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.settings.IntervallumSettings
import com.intervallum.ui.di.ConfigurationFactory
import com.intervallum.ui.di.configure
import com.intervallum.ui.main.MainIntent.DeleteTimer
import com.intervallum.ui.main.MainIntent.DuplicateTimer
import com.intervallum.ui.main.MainIntent.HidePermissionsDialog
import com.intervallum.ui.main.MainIntent.IgnoreNotificationsPermission
import com.intervallum.ui.main.MainIntent.RequestNotificationsPermission
import com.intervallum.ui.main.MainIntent.SwapTimers
import com.intervallum.ui.main.MainIntent.UpdateSortTimersBy
import com.intervallum.ui.main.MainState.Content
import com.intervallum.ui.main.MainState.Empty
import com.intervallum.util.toAgo
import intervallum.shared.generated.resources.Res
import intervallum.shared.generated.resources.never_run
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import kotlin.random.Random

internal class MainContainer(
    configurationFactory: ConfigurationFactory,
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val alertSettingsManager: AlertSettingsManager,
    private val intervallumSettings: IntervallumSettings,
    private val timerCreateFlow: SharedFlow<Long>
) : Container<MainState, MainIntent, MainAction> {

    override val store = store(MainState.Loading()) {
        configure(configurationFactory, "Main")
        whileSubscribed {
            launch {
                combine(
                    timerRepository.getShallowTimers(),
                    alertSettingsManager.alertSettings,
                    intervallumSettings.sortTimersBy
                ) { timers, settings, sortTimersBy ->
                    if (timers.isEmpty()) {
                        Empty(sortTimersBy)
                    } else {
                        val stateMainTimers = sortTimersBy.sort(timers).map { roomTimer ->
                            MainTimer(
                                id = roomTimer.id,
                                name = roomTimer.name,
                                duration = roomTimer.duration,
                                startedCount = roomTimer.startedCount,
                                completedCount = roomTimer.completedCount,
                                sortOrder = roomTimer.sortOrder,
                                lastRunFormatted = roomTimer.lastRun?.toAgo()
                                    ?: getString(Res.string.never_run)
                            )
                        }
                        val listItems = stateMainTimers.chunked(2).flatMap {
                            if (it.size == 2) {
                                // TODO this is a bug
                                it + Ad(Random.nextLong())
                            } else {
                                it
                            }
                        }
                        Content(
                            timers = listItems.toPersistentList(),
                            sortTimersBy = sortTimersBy,
                            showNotificationsPermissionRequest = settings.ignoreNotificationsPermissions.not() &&
                                permissionsHandler.getPermissionState(
                                    Permission.Notification
                                ) != PermissionState.Granted
                        )
                    }
                }.collect { updateState { it } }
            }
            launch {
                timerCreateFlow.collect {
                    action(MainAction.TimerUpdated(it))
                }
            }
        }

        reduce {
            when (it) {
                is DeleteTimer -> timerRepository.deleteTimer(it.mainTimer.id)

                is DuplicateTimer -> {
                    val newTimerId = timerRepository.duplicate(it.mainTimer.id)
                    action(MainAction.TimerUpdated(newTimerId))
                }

                HidePermissionsDialog -> {
                    updateState<Content, _> {
                        copy(showNotificationsPermissionRequest = false)
                    }
                }

                IgnoreNotificationsPermission -> alertSettingsManager.setIgnoreNotificationPermissions()

                RequestNotificationsPermission -> {
                    updateState<Content, _> {
                        copy(showNotificationsPermissionRequest = false)
                    }
                    permissionsHandler.requestPermission(Permission.Notification)
                }

                is SwapTimers -> timerRepository.swapTimers(
                    it.from.id, it.from.sortOrder, it.to.id, it.to.sortOrder
                )

                is UpdateSortTimersBy -> intervallumSettings.setSortTimersBy(it.sortTimersBy)
            }
        }
    }
}
