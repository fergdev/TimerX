package com.timerx.analytics

import co.touchlab.kermit.Logger
import com.timerx.settings.TimerXSettings
import com.timerx.settings.isEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TimerXAnalytics(private val timerXSettings: TimerXSettings) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var collectAnalytics = true

    init {
        coroutineScope.launch {
            timerXSettings.analytics.collect {
                collectAnalytics = it.isEnabled()
            }
        }
    }

    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        if (collectAnalytics) doLogEvent(eventName, params)
        else Logger.d { "Analytics disabled" }
    }

    internal abstract fun doLogEvent(eventName: String, params: Map<String, Any> = emptyMap())
}
