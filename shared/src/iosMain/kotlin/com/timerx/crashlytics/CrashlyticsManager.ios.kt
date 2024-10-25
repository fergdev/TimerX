package com.timerx.crashlytics

import co.touchlab.kermit.Logger
import com.timerx.settings.TimerXSettings
import com.timerx.settings.isEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
@Suppress("UseDataClass")
internal class CrashlyticsManager(
    private val timerXSettings: TimerXSettings,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        coroutineScope.launch {
            timerXSettings.analytics.collect {
                assert(crashlyticsIosCallback != null) {
                    "FirebaseCrashlyticsCallback not set"
                }
                Logger.d { "Setting crashlytics collection enabled to $it" }
                crashlyticsIosCallback!!.setCrashlyticsCollectionEnabled(it.isEnabled())
            }
        }
    }
}

interface CrashlyticsIosCallback {
    fun setCrashlyticsCollectionEnabled(enabled: Boolean)
}

private var crashlyticsIosCallback: CrashlyticsIosCallback? = null

@Suppress("Unused")
fun setCrashlyticsCallback(callback: CrashlyticsIosCallback) {
    crashlyticsIosCallback = callback
}
