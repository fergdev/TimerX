package com.timerx.analytics

actual fun getTimerXAnalytics() = object : ITimerXAnalytics {
    override fun logEvent(eventName: String, params: Map<String, Any>) {
        println("Logging $eventName")
    }
}