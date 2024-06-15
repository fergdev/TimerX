package com.timerx.analytics

expect class Analytics() {
    fun logEvent(eventName: String, params: Map<String, Any>?)
}