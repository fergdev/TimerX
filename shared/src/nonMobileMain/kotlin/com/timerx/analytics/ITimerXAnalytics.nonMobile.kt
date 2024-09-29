package com.timerx.analytics

object TimerXAnalytics : ITimerXAnalytics {
    override fun logEvent(eventName: String, params: Map<String, Any>) {
        println("Logggg $eventName")
    }
}
