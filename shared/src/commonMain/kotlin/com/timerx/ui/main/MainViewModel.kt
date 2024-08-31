package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.Permission
import com.timerx.permissions.PermissionState
import com.timerx.settings.TimerXSettings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
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
    val completedCount: Long
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

    private val domainTimers: MutableList<Timer> = mutableListOf()

    private fun MutableList<Timer>.toTimerInfos(): PersistentList<TimerInfo> {
        return this.map {
            TimerInfo(
                id = it.id,
                name = it.name,
                time = it.length().timeFormatted(),
                startedCount = it.stats.startedCount,
                completedCount = it.stats.completedCount,
            )
        }.toPersistentList()
    }

    private fun refreshData() {
        viewModelScope.launch {
            _stateFlow.update { it.copy(loadingTimers = true) }
            domainTimers.clear()
            domainTimers.addAll(timerRepository.getTimers())
            _stateFlow.update {
                it.copy(timers = domainTimers.toTimerInfos(), loadingTimers = false)
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
        viewModelScope.launch {
            timerRepository.deleteTimer(domainTimers.first {
                timerInfo.id == it.id
            })
            _stateFlow.update {
                domainTimers.removeAll { domainTimer ->
                    timerInfo.id == domainTimer.id
                }
                it.copy(timers = domainTimers.toTimerInfos(), loadingTimers = false)
            }
        }
    }

    private fun duplicateTimer(timer: TimerInfo) {
        viewModelScope.launch {
            timerRepository.duplicate(domainTimers.first {
                timer.id == it.id
            })
        }
        refreshData()
    }

    private fun swapTimer(from: Int, to: Int) {
        val fromTimer = domainTimers[from]
        val toTimer = domainTimers[to]
        domainTimers[from] = toTimer.copy(sortOrder = fromTimer.sortOrder)
        domainTimers[to] = fromTimer.copy(sortOrder = toTimer.sortOrder)
        _stateFlow.update { it.copy(timers = domainTimers.toTimerInfos()) }
        viewModelScope.launch {
            timerRepository.swapTimers(domainTimers[from], domainTimers[to])
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
