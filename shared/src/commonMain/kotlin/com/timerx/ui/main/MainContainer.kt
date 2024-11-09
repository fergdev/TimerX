package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.TimerXSettings
import com.timerx.ui.di.ConfigurationFactory
import com.timerx.ui.di.configure
import com.timerx.ui.main.MainIntent.DeleteTimer
import com.timerx.ui.main.MainIntent.DuplicateTimer
import com.timerx.ui.main.MainIntent.HidePermissionsDialog
import com.timerx.ui.main.MainIntent.IgnoreNotificationsPermission
import com.timerx.ui.main.MainIntent.RequestNotificationsPermission
import com.timerx.ui.main.MainIntent.SwapTimers
import com.timerx.ui.main.MainIntent.UpdateSortTimersBy
import com.timerx.ui.main.MainState.Content
import com.timerx.ui.main.MainState.Empty
import com.timerx.util.toAgo
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
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.never_run
import kotlin.random.Random

internal class MainContainer(
    configurationFactory: ConfigurationFactory,
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val alertSettingsManager: AlertSettingsManager,
    private val timerXSettings: TimerXSettings,
    private val timerCreateFlow: SharedFlow<Long>
) : Container<MainState, MainIntent, MainAction> {

    override val store = store(MainState.Loading()) {
        configure(configurationFactory, "Main")
        whileSubscribed {
            launch {
                combine(
                    timerRepository.getShallowTimers(),
                    alertSettingsManager.alertSettings,
                    timerXSettings.sortTimersBy
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

                is UpdateSortTimersBy -> timerXSettings.setSortTimersBy(it.sortTimersBy)
            }
        }
    }
}
