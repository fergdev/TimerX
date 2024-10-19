package com.timerx.analytics

import co.touchlab.kermit.Logger
import com.timerx.settings.TimerXSettings

class TimerXAnalyticsImpl(timerXSettings: TimerXSettings) : TimerXAnalytics(timerXSettings) {
    override fun doLogEvent(eventName: String, params: Map<String, Any>) {
        Logger.d { "Analytics no implemented" }
    }
}
