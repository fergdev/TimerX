@file:Suppress("Filename")
package com.timerx.analytics

import co.touchlab.kermit.Logger

object TimerXAnalytics : ITimerXAnalytics {
    override fun logEvent(eventName: String, params: Map<String, Any>) {
        Logger.d { "Analytics no implemented" }
    }
}
