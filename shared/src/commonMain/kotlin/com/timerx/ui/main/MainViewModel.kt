package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.TimerXSettings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MainViewModel(
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    data class State(
        val timers: ImmutableList<Timer> = persistentListOf(),
        val showNotificationsPermissionRequest: Boolean = false
    )

    class Interactions(
        val refreshData: () -> Unit,
        val deleteTimer: (Timer) -> Unit,
        val duplicateTimer: (Timer) -> Unit,
        val swapTimers: (Int, Int) -> Unit,
        val hidePermissionsDialog: () -> Unit,
        val requestNotificationsPermission: () -> Unit,
        val ignoreNotificationsPermission: () -> Unit
    )

    val interactions = Interactions(
        refreshData = ::refreshData,
        deleteTimer = ::deleteTimer,
        duplicateTimer = ::duplicateTimer,
        swapTimers = ::swapTimer,
        hidePermissionsDialog = ::hidePermissionsDialog,
        requestNotificationsPermission = ::requestNotificationsPermission,
        ignoreNotificationsPermission = ::ignoreNotificationsPermission
    )

    private val _stateFlow = MutableStateFlow(State())
    val state: StateFlow<State> = _stateFlow

    init {
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            val timers = timerRepository.getTimers().toPersistentList()
            _stateFlow.update { it.copy(timers = timers) }
        }
        viewModelScope.launch {
            timerXSettings.settings.collect {
                if (it.ignoreNotificationsPermissions.not()) {
                    if (permissionsHandler.getPermissionState(Permission.Notification) != PermissionState.Granted) {
                        _stateFlow.update { state ->
                            state.copy(showNotificationsPermissionRequest = true)
                        }
                    }
                }
            }
        }
    }

    private fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            timerRepository.deleteTimer(timer)
        }
        refreshData()
    }

    private fun duplicateTimer(timer: Timer) {
        viewModelScope.launch {
            timerRepository.duplicate(timer)
        }
        refreshData()
    }

    private fun swapTimer(from: Int, to: Int) {
        val timers = _stateFlow.value.timers.toMutableList()
        val fromTimer = timers[from]
        val toTimer = timers[to]
        timers[from] = toTimer.copy(sortOrder = fromTimer.sortOrder)
        timers[to] = fromTimer.copy(sortOrder = toTimer.sortOrder)
        _stateFlow.update { it.copy(timers = timers.toPersistentList()) }
        viewModelScope.launch {
            timerRepository.swapTimers(timers[from], timers[to])
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
}
