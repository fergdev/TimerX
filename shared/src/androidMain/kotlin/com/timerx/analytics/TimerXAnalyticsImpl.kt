package com.timerx.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.TimerXSettings

class TimerXAnalyticsImpl(
    timerXSettings: TimerXSettings,
    context: Context,
    txDispatchers: TxDispatchers
) : AbstractTimerXAnalytics(timerXSettings, txDispatchers) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun doLogEvent(eventName: String, params: Map<String, Any>) {
        val bundle: Bundle? = params.let {
            if (it.isEmpty()) {
                Bundle.EMPTY
            } else {
                Bundle().load(params)
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun doLogScreen(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }

    override fun doLogException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}

internal fun Bundle.load(params: Map<String, Any>): Bundle {
    params.forEach { (t, u) ->
        when (u) {
            is String -> this.putString(t, u)
            is Int -> this.putInt(t, u)
            is Long -> this.putLong(t, u)
            is Float -> this.putFloat(t, u)
            is Double -> this.putDouble(t, u)
            else -> this.putString(t, u.toString())
        }
    }
    return this
}
