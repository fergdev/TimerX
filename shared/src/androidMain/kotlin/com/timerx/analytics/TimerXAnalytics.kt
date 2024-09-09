package com.timerx.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.mp.KoinPlatform

class TimerXAnalytics : ITimerXAnalytics {
    private val context: Context = KoinPlatform.getKoin().get()
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val bundle: Bundle? = params.let {
            if (it.isEmpty()) {
                Bundle.EMPTY
            } else {
                Bundle().apply {
                    it.forEach { (t, u) ->
                        when (u) {
                            is String -> this.putString(t, u)
                            is Int -> this.putInt(t, u)
                            is Long -> this.putLong(t, u)
                            is Float -> this.putFloat(t, u)
                            is Double -> this.putDouble(t, u)
                            else -> this.putString(t, u.toString())
                        }
                    }
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}

actual fun getTimerXAnalytics(): ITimerXAnalytics = TimerXAnalytics()
