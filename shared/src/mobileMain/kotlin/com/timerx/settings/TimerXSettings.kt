package com.timerx.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.timerx.domain.SortTimersBy
import com.timerx.domain.toSortTimersBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerXSettings(private val dataStore: DataStore<Preferences>) : ITimerXSettings {

    companion object {
        private const val PREFS_TAG_KEY = "app_preferences"
        private const val VOLUME = "volume"
        private const val VIBRATION_ENABLE = "vibration"
        private const val IGNORE_NOTIFICATION_PERMISSIONS = "ignore_notification_permissions"
        private const val SORT_TIMERS_BY = "sort_timers_by"
    }

    private val volumePreferencesKey = floatPreferencesKey("$PREFS_TAG_KEY$VOLUME")
    private val vibrationEnablePreferencesKey =
        booleanPreferencesKey("$PREFS_TAG_KEY$VIBRATION_ENABLE")
    private val ignoreNotificationsPermissionsKey =
        booleanPreferencesKey("$PREFS_TAG_KEY$IGNORE_NOTIFICATION_PERMISSIONS")
    private val sortTimersByKey =
        intPreferencesKey("$PREFS_TAG_KEY$SORT_TIMERS_BY")

    override val settings: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            volume = preferences[volumePreferencesKey] ?: 1F,
            vibrationEnabled = preferences[vibrationEnablePreferencesKey] ?: true,
            ignoreNotificationsPermissions = preferences[ignoreNotificationsPermissionsKey]
                ?: false,
            sortTimersBy = preferences[sortTimersByKey]?.toSortTimersBy() ?: SortTimersBy.SORT_ORDER
        )
    }

    override suspend fun setVolume(volume: Float) {
        dataStore.edit { preferences ->
            preferences[volumePreferencesKey] = volume
        }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[vibrationEnablePreferencesKey] = enabled
        }
    }

    override suspend fun setIgnoreNotificationPermissions() {
        dataStore.edit { preferences ->
            preferences[ignoreNotificationsPermissionsKey] = true
        }
    }

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) {
        dataStore.edit { preferences ->
            preferences[sortTimersByKey] = sortTimersBy.ordinal
        }
    }
}