package com.timerx.crashlytics

import com.timerx.settings.TimerXSettings
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
            timerXSettings.collectAnalytics.collect {
                assert(crashlyticsIosCallback != null){
                    "FirebaseCrashlyticsCallback not set"
                }
                crashlyticsIosCallback!!.setCrashlyticsCollectionEnabled(it)
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
