package com.timerx.ui.main

import com.timerx.database.ITimerRepository
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class TimerInfo(
    val id: Long,
    val name: String,
    val duration: Long,
    val startedCount: Long,
    val completedCount: Long,
    val sortOrder: Long,
    val lastRunMillis: Long,
    val lastRunFormatted: String,
)

class MainViewModel(
    private val timerRepository: ITimerRepository,
    private val permissionsHandler: IPermissionsHandler,
    private val timerXSettings: TimerXSettings
) : ViewModel() {

    data class State(
        val loadingTimers: Boolean = false,
        val timers: ImmutableList<TimerInfo> = persistentListOf(),
        val sortTimersBy: SortTimersBy = SortTimersBy.SORT_ORDER,
        val showNotificationsPermissionRequest: Boolean = false
    )

    class Interactions(
        val deleteTimer: (TimerInfo) -> Unit,
        val duplicateTimer: (TimerInfo) -> Unit,
        val swapTimers: (TimerInfo, TimerInfo) -> Unit,
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
                .collect { roomTimers ->
                    _stateFlow.update {
                        val timers = roomTimers.map { roomTimer ->
                            TimerInfo(
                                id = roomTimer.id,
                                name = roomTimer.name,
                                duration = roomTimer.duration,
                                startedCount = roomTimer.startedCount,
                                completedCount = roomTimer.completedCount,
                                sortOrder = roomTimer.sortOrder,
                                lastRunMillis = roomTimer.lastRun?.toEpochMilliseconds() ?: 0L,
                                lastRunFormatted = roomTimer.lastRun?.toLocalDateTime(
                                    TimeZone.currentSystemDefault()
                                )?.format(dateTimeFormat) ?: "Never"
                            )
                        }
                        it.copy(
                            loadingTimers = false,
                            timers = _stateFlow.value.sortTimersBy.sort(timers).toPersistentList()
                        )
                    }
                }
        }
        viewModelScope.launch {
            timerXSettings.settings.collect { settings ->
                _stateFlow.update {
                    it.copy(
                        sortTimersBy = settings.sortTimersBy,
                        timers = settings.sortTimersBy.sort(_stateFlow.value.timers)
                            .toPersistentList()
                    )
                }
                if (settings.ignoreNotificationsPermissions.not()) {
                    if (permissionsHandler.getPermissionState(Permission.Notification) != PermissionState.Granted) {
                        _stateFlow.update { state ->
                            state.copy(showNotificationsPermissionRequest = true)
                        }
                    }
                }
            }
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyy-MM-dd HH:mm:ss")
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

    private fun updateSortTimersBy(sortTimersBy: SortTimersBy) {
        viewModelScope.launch {
            timerXSettings.setSortTimersBy(sortTimersBy)
        }
    }
}
