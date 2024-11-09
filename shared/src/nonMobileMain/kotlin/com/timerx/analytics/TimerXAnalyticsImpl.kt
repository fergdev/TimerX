package com.timerx.analytics

import co.touchlab.kermit.Logger
import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.TimerXSettings
import com.timerx.util.KoverIgnore

@KoverIgnore("Analytics not implemented on this platform")
class TimerXAnalyticsImpl(timerXSettings: TimerXSettings, txDispatchers: TxDispatchers) :
    AbstractTimerXAnalytics(timerXSettings, txDispatchers) {
    override fun doLogEvent(eventName: String, params: Map<String, Any>) {
        Logger.v { "Logging eventName='$eventName' params='$params'" }
    }

    override fun doLogScreen(screenName: String) {
        Logger.v { "Logging screen='$screenName'" }
    }

    override fun doLogException(throwable: Throwable) {
        Logger.v { "Logging exception '$throwable'" }
    }
}
