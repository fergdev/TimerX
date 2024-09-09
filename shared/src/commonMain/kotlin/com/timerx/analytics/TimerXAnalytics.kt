package com.timerx.analytics

expect fun getTimerXAnalytics(): ITimerXAnalytics

interface ITimerXAnalytics {
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
}
