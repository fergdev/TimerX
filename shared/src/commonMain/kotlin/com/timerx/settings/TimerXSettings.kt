package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.domain.SortTimersBy
import com.timerx.platform.platformCapabilities
import com.timerx.util.assert
import com.timerx.util.mapIfNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TimerXSettings {
    val alertSettingsManager: AlertSettingsManager
    val themeSettingsManager: ThemeSettingsManager
    val backgroundSettingsManager: BackgroundSettingsManager
    val sortTimersBy: Flow<SortTimersBy>
    val keepScreenOn: Flow<Boolean>
    val analytics: Flow<AnalyticsSettings>
    suspend fun setKeepScreenOn(keepScreenOn: Boolean)
    suspend fun setSortTimersBy(sortTimersBy: SortTimersBy)
    suspend fun setCollectAnalytics(collectAnalytics: Boolean)
}

sealed interface AnalyticsSettings {
    data class Available(val enabled: Boolean) : AnalyticsSettings
    data object NotAvailable : AnalyticsSettings
}

fun AnalyticsSettings.isEnabled(): Boolean =
    this is AnalyticsSettings.Available && enabled

private const val SORT_TIMERS_BY = "sortTimersBy"
private const val KEEP_SCREEN_ON = "keepScreenOn"
private const val COLLECT_ANALYTICS = "collectAnalytics"

@OptIn(ExperimentalSettingsApi::class)
internal class TimerXSettingsImpl : TimerXSettings {
    private val flowSettings = Settings().makeObservable().toFlowSettings(Dispatchers.Main)

    override val alertSettingsManager = AlertSettingsManagerImpl(
        flowSettings = flowSettings,
        platformCapabilities = platformCapabilities
    )

    override val sortTimersBy = flowSettings.getIntOrNullFlow(SORT_TIMERS_BY).map {
        if (it == null) SortTimersBy.SORT_ORDER
        else SortTimersBy.entries[it]
    }
    override val keepScreenOn: Flow<Boolean> =
        flowSettings.getBooleanOrNullFlow(KEEP_SCREEN_ON).mapIfNull(true)

    override val analytics: Flow<AnalyticsSettings> =
        flowSettings.getBooleanOrNullFlow(COLLECT_ANALYTICS).map {
            if (!platformCapabilities.hasAnalytics) AnalyticsSettings.NotAvailable
            if (it == null) AnalyticsSettings.Available(false)
            else AnalyticsSettings.Available(it)
        }

    override suspend fun setKeepScreenOn(keepScreenOn: Boolean) =
        flowSettings.putBoolean(KEEP_SCREEN_ON, keepScreenOn)

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) =
        flowSettings.putInt(SORT_TIMERS_BY, sortTimersBy.ordinal)

    override suspend fun setCollectAnalytics(collectAnalytics: Boolean) {
        assert(platformCapabilities.hasAnalytics) {
            "Analytics is not available on this platform"
        }
        flowSettings.putBoolean(COLLECT_ANALYTICS, collectAnalytics)
    }

    override val themeSettingsManager: ThemeSettingsManager = ThemeSettingsManagerImpl(flowSettings)
    override val backgroundSettingsManager: BackgroundSettingsManager =
        BackgroundSettingsManagerImpl(flowSettings)
}
