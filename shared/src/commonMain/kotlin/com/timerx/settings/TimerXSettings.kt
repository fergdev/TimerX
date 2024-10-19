package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.domain.SortTimersBy
import com.timerx.util.mapIfNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class AlertSettings(
    val volume: Float,
    val vibrationEnabled: Boolean,
    val ignoreNotificationsPermissions: Boolean,
    val ttsVoiceName: String?,
)

interface TimerXSettings {
    val alertSettingsManager: AlertSettingsManager
    val themeSettingsManager: ThemeSettingsManager
    val backgroundSettingsManager: BackgroundSettingsManager
    val sortTimersBy: Flow<SortTimersBy>
    val keepScreenOn: Flow<Boolean>
    val collectAnalytics: Flow<Boolean>
    suspend fun setKeepScreenOn(keepScreenOn: Boolean)
    suspend fun setSortTimersBy(sortTimersBy: SortTimersBy)
    suspend fun setCollectAnalytics(collectAnalytics: Boolean)
}

private const val SORT_TIMERS_BY = "sortTimersBy"
private const val KEEP_SCREEN_ON = "keepScreenOn"
private const val COLLECT_ANALYTICS = "collectAnalytics"

@OptIn(ExperimentalSettingsApi::class)
internal class TimerXSettingsImpl : TimerXSettings {
    private val flowSettings = Settings().makeObservable().toFlowSettings(Dispatchers.Main)

    override val alertSettingsManager = AlertSettingsManagerImpl(flowSettings)

    override val sortTimersBy = flowSettings.getIntOrNullFlow(SORT_TIMERS_BY).map {
        if (it == null) SortTimersBy.SORT_ORDER
        else SortTimersBy.entries[it]
    }
    override val keepScreenOn: Flow<Boolean> =
        flowSettings.getBooleanOrNullFlow(KEEP_SCREEN_ON).mapIfNull(true)

    override val collectAnalytics: Flow<Boolean> =
        flowSettings.getBooleanOrNullFlow(COLLECT_ANALYTICS).mapIfNull(true)

    override suspend fun setKeepScreenOn(keepScreenOn: Boolean) {
        flowSettings.putBoolean(KEEP_SCREEN_ON, keepScreenOn)
    }

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) {
        flowSettings.putInt(SORT_TIMERS_BY, sortTimersBy.ordinal)
    }

    override suspend fun setCollectAnalytics(collectAnalytics: Boolean) {
        flowSettings.putBoolean(COLLECT_ANALYTICS, collectAnalytics)
    }

    override val themeSettingsManager: ThemeSettingsManager = ThemeSettingsManagerImpl(flowSettings)
    override val backgroundSettingsManager: BackgroundSettingsManager =
        BackgroundSettingsManagerImpl(flowSettings)
}
