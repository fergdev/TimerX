package com.timerx.settings

import com.timerx.domain.SortTimersBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object InMemoryTimerXSettings : ITimerXSettings {
    private val flow = MutableStateFlow(
        Settings(
            volume = 1f,
            vibrationEnabled = false,
            ignoreNotificationsPermissions = true,
            sortTimersBy = SortTimersBy.SORT_ORDER
        )
    )
    override val settings: Flow<Settings>
        get() = flow

    override suspend fun setVolume(volume: Float) {
        flow.update {
            it.copy(volume = volume)
        }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        flow.update {
            it.copy(vibrationEnabled = enabled)
        }
    }

    override suspend fun setIgnoreNotificationPermissions() {
        flow.update {
            it.copy(ignoreNotificationsPermissions = true)
        }
    }

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) {
        flow.update {
            it.copy(sortTimersBy = sortTimersBy)
        }
    }
}