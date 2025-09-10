package com.intervallum.crashlytics

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.intervallum.settings.IntervallumSettings
import com.intervallum.settings.isEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UseDataClass")
internal class CrashlyticsManager(
    private val intervallumSettings: IntervallumSettings,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        coroutineScope.launch {
            intervallumSettings.analytics.collect {
                Firebase.crashlytics.isCrashlyticsCollectionEnabled = it.isEnabled()
            }
        }
    }
}
