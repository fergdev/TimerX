package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.domain.SortTimersBy
import com.timerx.util.mapIfNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.russhwolf.settings.Settings as MPSettings

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

private const val VIBRATION_ENABLED = "vibrationEnabled"
private const val VOLUME = "volume"
private const val SET_IGNORE_NOTIFICATIONS_PERMISSION = "setIgnoreNotificationsPermission"
private const val SORT_TIMERS_BY = "sortTimersBy"

@OptIn(ExperimentalSettingsApi::class)
class TimerXSettings : ITimerXSettings {
    private val flowSettings = MPSettings().makeObservable().toFlowSettings(Dispatchers.Main)
    private val volume = flowSettings.getFloatOrNullFlow(VOLUME).mapIfNull(1F)
    private val vibrationEnabled = flowSettings.getBooleanOrNullFlow(VIBRATION_ENABLED).mapIfNull(true)
    private val ignoreNotificationsPermissions =
        flowSettings.getBooleanOrNullFlow(SET_IGNORE_NOTIFICATIONS_PERMISSION).mapIfNull(false)
    private val sortTimersBy = flowSettings.getIntOrNullFlow(SORT_TIMERS_BY).mapIfNull(0)

    override val settings: Flow<Settings> = combine(
        volume, vibrationEnabled, ignoreNotificationsPermissions, sortTimersBy
    ) { a, b, c, d ->
        Settings(
            volume = a,
            vibrationEnabled = b,
            ignoreNotificationsPermissions = c,
            sortTimersBy = SortTimersBy.entries[d]
        )
    }

    override suspend fun setVolume(volume: Float) {
        flowSettings.putFloat(VOLUME, volume)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        flowSettings.putBoolean(VIBRATION_ENABLED, enabled)
    }

    override suspend fun setIgnoreNotificationPermissions() {
        flowSettings.putBoolean(SET_IGNORE_NOTIFICATIONS_PERMISSION, true)
    }

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) {
        flowSettings.putInt(SORT_TIMERS_BY, sortTimersBy.ordinal)
    }
}