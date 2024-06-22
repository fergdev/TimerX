package com.timerx.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Settings(
    val volume: Float,
    val vibrationEnabled: Boolean
)

class TimerXSettings(private val dataStore: DataStore<Preferences>) {

    companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val VOLUME = "volume"
        private const val VIBRATION_ENABLE = "vibration"
    }

    private val volumePreferencesKey = floatPreferencesKey("$PREFS_TAG_KEY$VOLUME")
    private val vibrationEnablePreferencesKey =
        booleanPreferencesKey("$PREFS_TAG_KEY$VIBRATION_ENABLE")

    val settings: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            volume = preferences[volumePreferencesKey] ?: 1F,
            vibrationEnabled = preferences[vibrationEnablePreferencesKey] ?: true
        )
    }

    suspend fun setVolume(volume: Float) {
        dataStore.edit { preferences ->
            preferences[volumePreferencesKey] = volume
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[vibrationEnablePreferencesKey] = enabled
        }
    }
}
