package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.TimerXSettings
import com.timerx.time.toAgo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.random.Random

sealed interface MainListItem {
    val id: Long
}

data class Timer(
    override val id: Long,
    val name: String,
    val duration: Long,
    val startedCount: Long,
    val completedCount: Long,
    val sortOrder: Long,
    val lastRunMillis: Long,
    val lastRunFormatted: String,
) : MainListItem

data class Ad(override val id: Long) : MainListItem

class MainViewModel(
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    data class State(
        val loadingTimers: Boolean = false,
        val timers: ImmutableList<MainListItem> = persistentListOf(),
        val sortTimersBy: SortTimersBy = SortTimersBy.SORT_ORDER,
        val showNotificationsPermissionRequest: Boolean = false
    )

    class Interactions(
        val deleteTimer: (Timer) -> Unit,
        val duplicateTimer: (Timer) -> Unit,
        val swapTimers: (Timer, Timer) -> Unit,
        val hidePermissionsDialog: () -> Unit,
        val requestNotificationsPermission: () -> Unit,
        val ignoreNotificationsPermission: () -> Unit,
        val updateSortTimersBy: (SortTimersBy) -> Unit
    )

    val interactions = Interactions(
        deleteTimer = ::deleteTimer,
        duplicateTimer = ::duplicateTimer,
        swapTimers = ::swapTimer,
        hidePermissionsDialog = ::hidePermissionsDialog,
        requestNotificationsPermission = ::requestNotificationsPermission,
        ignoreNotificationsPermission = ::ignoreNotificationsPermission,
        updateSortTimersBy = ::updateSortTimersBy
    )

    private val _stateFlow = MutableStateFlow(State())
    val state: StateFlow<State> = _stateFlow

    init {
        viewModelScope.launch {
            _stateFlow.update { it.copy(loadingTimers = true) }
            timerRepository.getShallowTimers()
                .combine(
                    timerXSettings.settings
                ) { timers, settings ->
                    val stateTimers = settings.sortTimersBy.sort(timers).map { roomTimer ->
                        Timer(
                            id = roomTimer.id,
                            name = roomTimer.name,
                            duration = roomTimer.duration,
                            startedCount = roomTimer.startedCount,
                            completedCount = roomTimer.completedCount,
                            sortOrder = roomTimer.sortOrder,
                            lastRunMillis = roomTimer.lastRun?.toEpochMilliseconds() ?: 0L,
                            lastRunFormatted = roomTimer.lastRun?.toAgo() ?: "Never"
                        )
                    }
                    val listItems = stateTimers.chunked(2)
                        .flatMap {
                            if (it.size == 2) {
                                it + Ad(Random.nextLong())
                            } else {
                                it
                            }
                        }
                    _stateFlow.value.copy(
                        loadingTimers = false,
                        timers = listItems.toPersistentList(),
                        sortTimersBy = settings.sortTimersBy,
                        showNotificationsPermissionRequest =
                        settings.ignoreNotificationsPermissions.not() &&
                                permissionsHandler.getPermissionState(Permission.Notification) != PermissionState.Granted
                    )
                }.collect { newState ->
                    _stateFlow.value = newState
                }
        }
    }

    private fun deleteTimer(timer: Timer) {
        viewModelScope.launch { timerRepository.deleteTimer(timer.id) }
    }

    private fun duplicateTimer(timer: Timer) {
        viewModelScope.launch { timerRepository.duplicate(timer.id) }
    }

    private fun swapTimer(from: Timer, to: Timer) {
        viewModelScope.launch {
            timerRepository.swapTimers(
                from.id,
                from.sortOrder,
                to.id,
                to.sortOrder
            )
        }
    }

    private fun hidePermissionsDialog() {
        _stateFlow.update { it.copy(showNotificationsPermissionRequest = false) }
    }

    private fun requestNotificationsPermission() {
        viewModelScope.launch {
            permissionsHandler.requestPermission(Permission.Notification)
            hidePermissionsDialog()
        }
    }

    private fun ignoreNotificationsPermission() {
        viewModelScope.launch {
            _stateFlow.update { it.copy(showNotificationsPermissionRequest = false) }
            timerXSettings.setIgnoreNotificationPermissions()
        }
    }

    private fun updateSortTimersBy(sortTimersBy: SortTimersBy) {
        viewModelScope.launch {
            timerXSettings.setSortTimersBy(sortTimersBy)
        }
    }
}
