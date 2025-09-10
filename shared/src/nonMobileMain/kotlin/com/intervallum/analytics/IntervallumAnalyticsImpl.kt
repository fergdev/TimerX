package com.intervallum.analytics

import co.touchlab.kermit.Logger
import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.IntervallumSettings
import com.intervallum.util.KoverIgnore

@KoverIgnore("Analytics not implemented on this platform")
class IntervallumAnalyticsImpl(intervallumSettings: IntervallumSettings, txDispatchers: TxDispatchers) :
    AbstractIntervallumAnalytics(intervallumSettings, txDispatchers) {
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
