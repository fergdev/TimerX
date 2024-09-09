package com.timerx.ui.main

import com.timerx.domain.SortTimersBy
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

internal sealed interface MainState : MVIState {
    val sortTimersBy: SortTimersBy

    data class Loading(
        override val sortTimersBy: SortTimersBy = SortTimersBy.SORT_ORDER
    ) : MainState

    data class Empty(
        override val sortTimersBy: SortTimersBy = SortTimersBy.SORT_ORDER
    ) : MainState

    data class Content(
        override val sortTimersBy: SortTimersBy = SortTimersBy.SORT_ORDER,
        val timers: ImmutableList<MainListItem> = persistentListOf(),
        val showNotificationsPermissionRequest: Boolean = false
    ) : MainState
}

internal sealed interface MainListItem {
    val id: Long
}

internal data class MainTimer(
    override val id: Long,
    val name: String,
    val duration: Long,
    val startedCount: Long,
    val completedCount: Long,
    val sortOrder: Long,
    val lastRunMillis: Long,
    val lastRunFormatted: String,
) : MainListItem

internal data class Ad(override val id: Long) : MainListItem

internal sealed interface MainIntent : MVIIntent {
    data class DeleteTimer(val mainTimer: MainTimer) : MainIntent
    data class DuplicateTimer(val mainTimer: MainTimer) : MainIntent
    data class SwapTimers(val from: MainTimer, val to: MainTimer) : MainIntent
    data object HidePermissionsDialog : MainIntent
    data object RequestNotificationsPermission : MainIntent
    data object IgnoreNotificationsPermission : MainIntent
    data class UpdateSortTimersBy(val sortTimersBy: SortTimersBy) : MainIntent
}
