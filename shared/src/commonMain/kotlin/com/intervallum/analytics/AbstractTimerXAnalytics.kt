package com.intervallum.analytics

import co.touchlab.kermit.Logger
import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.IntervallumSettings
import com.intervallum.settings.isEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface IntervallumAnalytics {
    fun logScreen(screenName: String)
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
    fun logException(throwable: Throwable)
}

abstract class AbstractIntervallumAnalytics(
    private val intervallumSettings: IntervallumSettings,
    txDispatchers: TxDispatchers
) : IntervallumAnalytics {
    private val coroutineScope = CoroutineScope(txDispatchers.main)
    private var collectAnalytics = true

    init {
        coroutineScope.launch {
            intervallumSettings.analytics.collect {
                collectAnalytics = it.isEnabled()
            }
        }
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        if (collectAnalytics) doLogEvent(eventName, params)
        else Logger.d { "Analytics disabled for logEvent" }
    }

    abstract fun doLogEvent(eventName: String, params: Map<String, Any> = emptyMap())

    override fun logScreen(screenName: String) {
        if (collectAnalytics) doLogScreen(screenName)
        else Logger.d { "Analytics disabled for logScreen" }
    }

    abstract fun doLogScreen(screenName: String)

    override fun logException(throwable: Throwable) {
        if (collectAnalytics) doLogException(throwable)
        else Logger.d { "Analytics disabled for logException" }
    }

    abstract fun doLogException(throwable: Throwable)
}
