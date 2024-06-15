package com.timerx.analytics

expect class TimerXAnalytics() {
    fun logEvent(eventName: String, params: Map<String, Any>?)
}