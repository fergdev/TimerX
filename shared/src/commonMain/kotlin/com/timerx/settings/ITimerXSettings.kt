package com.timerx.settings

import com.timerx.domain.SortTimersBy
import kotlinx.coroutines.flow.Flow

data class Settings(
    val volume: Float,
    val vibrationEnabled: Boolean,
    val ignoreNotificationsPermissions: Boolean,
    val sortTimersBy: SortTimersBy
)

interface ITimerXSettings {
    val settings: Flow<Settings>

    suspend fun setVolume(volume: Float)

    suspend fun setVibrationEnabled(enabled: Boolean)

    suspend fun setIgnoreNotificationPermissions()

    suspend fun setSortTimersBy(sortTimersBy: SortTimersBy)
}
