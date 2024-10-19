package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.ITimerXSettings
import com.timerx.util.toAgo
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import kotlin.random.Random

internal class MainContainer(
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val timerXSettings: ITimerXSettings,
    private val timerCreateFlow: SharedFlow<Long>
) : Container<MainState, MainIntent, MainAction> {

    override val store = store(MainState.Loading()) {
        whileSubscribed {
            launch {
                combine(
                    timerRepository.getShallowTimers(),
                    timerXSettings.alertSettingsManager.alertSettings,
                    timerXSettings.sortTimersBy
                ) { timers, settings, sortTimersBy ->
                    if (timers.isEmpty()) {
                        MainState.Empty(sortTimersBy)
                    } else {
                        val stateMainTimers = sortTimersBy.sort(timers).map { roomTimer ->
                            MainTimer(
                                id = roomTimer.id,
                                name = roomTimer.name,
                                duration = roomTimer.duration,
                                startedCount = roomTimer.startedCount,
                                completedCount = roomTimer.completedCount,
                                sortOrder = roomTimer.sortOrder,
                                lastRunFormatted = roomTimer.lastRun?.toAgo() ?: "Never run"
                            )
                        }
                        val listItems = stateMainTimers.chunked(2)
                            .flatMap {
                                if (it.size == 2) {
                                    // TODO this is a bug
                                    it + Ad(Random.nextLong())
                                } else {
                                    it
                                }
                            }
                        MainState.Content(
                            timers = listItems.toPersistentList(),
                            sortTimersBy = sortTimersBy,
                            showNotificationsPermissionRequest =
                            settings.ignoreNotificationsPermissions.not() &&
                                permissionsHandler.getPermissionState(Permission.Notification)
                                != PermissionState.Granted
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
                is MainIntent.DeleteTimer ->
                    timerRepository.deleteTimer(it.mainTimer.id)

                is MainIntent.DuplicateTimer ->
                    timerRepository.duplicate(it.mainTimer.id)

                MainIntent.HidePermissionsDialog -> {
                    updateState<MainState.Content, _> {
                        copy(showNotificationsPermissionRequest = false)
                    }
                }

                MainIntent.IgnoreNotificationsPermission ->
                    timerXSettings.alertSettingsManager.setIgnoreNotificationPermissions()

                MainIntent.RequestNotificationsPermission -> {
                    updateState<MainState.Content, _> {
                        copy(showNotificationsPermissionRequest = false)
                    }
                    permissionsHandler.requestPermission(Permission.Notification)
                }

                is MainIntent.SwapTimers ->
                    timerRepository.swapTimers(
                        it.from.id,
                        it.from.sortOrder,
                        it.to.id,
                        it.to.sortOrder
                    )

                is MainIntent.UpdateSortTimersBy ->
                    timerXSettings.setSortTimersBy(it.sortTimersBy)
            }
        }
    }
}
