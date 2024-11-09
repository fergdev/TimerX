package com.timerx.analytics

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.TimerXSettings

class TimerXAnalyticsImpl(timerXSettings: TimerXSettings, txDispatchers: TxDispatchers) :
    AbstractTimerXAnalytics(timerXSettings, txDispatchers) {

    override fun doLogEvent(eventName: String, params: Map<String, Any>) {
        firebaseIosCallback?.logEvent(eventName, params.toLogString())
    }

    override fun doLogScreen(screenName: String) {
        firebaseIosCallback?.logScreen(screenName)
    }

    override fun doLogException(throwable: Throwable) {
        firebaseIosCallback?.logError(throwable.toString())
    }

    private fun Map<String, Any>?.toLogString(): String {
        if (this == null) return ""

        val sb = StringBuilder()
        this.forEach {
            sb.append("${it.key}:${it.value},")
        }
        return sb.toString()
    }
}

interface FirebaseIosCallback {
    fun logEvent(eventId: String, params: String)
    fun logScreen(screenName: String)
    fun logError(error: String)
}

private var firebaseIosCallback: FirebaseIosCallback? = null

// Callback from swift that allows settings of firebase callback
@Suppress("unused")
fun firebaseCallback(callback: FirebaseIosCallback) {
    firebaseIosCallback = callback
}
