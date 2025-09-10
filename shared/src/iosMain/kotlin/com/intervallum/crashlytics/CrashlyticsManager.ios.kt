package com.intervallum.crashlytics

import co.touchlab.kermit.Logger
import com.intervallum.settings.IntervallumSettings
import com.intervallum.settings.isEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
@Suppress("UseDataClass")
internal class CrashlyticsManager(
    private val intervallumSettings: IntervallumSettings,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        coroutineScope.launch {
            intervallumSettings.analytics.collect {
                require(crashlyticsIosCallback != null) {
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
