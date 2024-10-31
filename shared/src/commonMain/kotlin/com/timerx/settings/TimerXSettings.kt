package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.timerx.domain.SortTimersBy
import com.timerx.platform.PlatformCapabilities
import com.timerx.util.mapIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TimerXSettings {
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
internal class TimerXSettingsImpl(
    private val platformCapabilities: PlatformCapabilities,
    private val flowSettings:FlowSettings
) : TimerXSettings {

    override val sortTimersBy = flowSettings.getIntOrNullFlow(SORT_TIMERS_BY).map {
        if (it == null) SortTimersBy.SORT_ORDER
        else SortTimersBy.entries[it]
    }
    override val keepScreenOn: Flow<Boolean> =
        flowSettings.getBooleanOrNullFlow(KEEP_SCREEN_ON).mapIfNull(true)

    override val analytics: Flow<AnalyticsSettings> =
        flowSettings.getBooleanOrNullFlow(COLLECT_ANALYTICS).map {
            if (!platformCapabilities.hasAnalytics) AnalyticsSettings.NotAvailable
            else if (it == null) AnalyticsSettings.Available(true)
            else AnalyticsSettings.Available(it)
        }

    override suspend fun setKeepScreenOn(keepScreenOn: Boolean) =
        flowSettings.putBoolean(KEEP_SCREEN_ON, keepScreenOn)

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) =
        flowSettings.putInt(SORT_TIMERS_BY, sortTimersBy.ordinal)

    override suspend fun setCollectAnalytics(collectAnalytics: Boolean) {
        require(platformCapabilities.hasAnalytics) {
            "Analytics are not available on this platform"
        }
        flowSettings.putBoolean(COLLECT_ANALYTICS, collectAnalytics)
    }
}
