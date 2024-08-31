package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.timeFormatted
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

data class TimerInfo(
    val id: Long,
    val name: String,
    val time: String,
    val startedCount: Long,
    val completedCount: Long,
    val sortOrder: Long
)

class MainViewModel(
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    data class State(
        val loadingTimers: Boolean = false,
        val timers: ImmutableList<TimerInfo> = persistentListOf(),
        val showNotificationsPermissionRequest: Boolean = false
    )

    class Interactions(
        val refreshData: () -> Unit,
        val deleteTimer: (TimerInfo) -> Unit,
        val duplicateTimer: (TimerInfo) -> Unit,
        val swapTimers: (TimerInfo, TimerInfo) -> Unit,
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
            _stateFlow.update { it.copy(loadingTimers = true) }
            timerRepository.getShallowTimers()
                .collect { roomTimers ->
                    _stateFlow.update {
                        it.copy(
                            loadingTimers = false,
                            timers = roomTimers.map { roomTimer ->
                                TimerInfo(
                                    id = roomTimer.id,
                                    name = roomTimer.name,
                                    time = roomTimer.duration.toInt().timeFormatted(),
                                    startedCount = 1,
                                    completedCount = 1,
                                    sortOrder = roomTimer.sortOrder
                                )
                            }.toPersistentList()
                        )
                    }
                }
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

    private fun deleteTimer(timerInfo: TimerInfo) {
        viewModelScope.launch { timerRepository.deleteTimer(timerInfo.id) }
    }

    private fun duplicateTimer(timerInfo: TimerInfo) {
        viewModelScope.launch { timerRepository.duplicate(timerInfo.id) }
    }

    private fun swapTimer(from: TimerInfo, to: TimerInfo) {
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
}
