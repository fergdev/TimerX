package com.timerx.crashlytics

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.timerx.settings.TimerXSettings
import com.timerx.settings.isEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UseDataClass")
internal class CrashlyticsManager(
    private val timerXSettings: TimerXSettings,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        coroutineScope.launch {
            timerXSettings.analytics.collect {
                Firebase.crashlytics.isCrashlyticsCollectionEnabled = it.isEnabled()
            }
        }
    }
}
