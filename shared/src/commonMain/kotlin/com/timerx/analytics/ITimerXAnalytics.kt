package com.timerx.analytics

interface ITimerXAnalytics {
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
}
