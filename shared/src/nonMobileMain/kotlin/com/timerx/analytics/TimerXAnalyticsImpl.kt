package com.timerx.analytics

import co.touchlab.kermit.Logger
import com.timerx.settings.TimerXSettings
import com.timerx.util.KoverIgnore

@KoverIgnore("Analytics not implemented on this platform")
class TimerXAnalyticsImpl(timerXSettings: TimerXSettings) : TimerXAnalytics(timerXSettings) {
    override fun doLogEvent(eventName: String, params: Map<String, Any>) {
        Logger.d { "Analytics not implemented" }
    }
}
