package com.timerx.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.mp.KoinPlatform

actual class Analytics {
    private val context: Context = KoinPlatform.getKoin().get()
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    actual fun logEvent(eventName: String, params: Map<String, Any>?) {
        println("Logging $eventName")
        val bundle :Bundle? = params?.run {
            val bundle = Bundle()
            this.forEach { (t, u) ->
                when (u) {
                    is String -> bundle.putString(t, u)
                    is Int -> bundle.putInt(t, u)
                    is Long -> bundle.putLong(t, u)
                    is Float -> bundle.putFloat(t, u)
                    is Double -> bundle.putDouble(t, u)
                    else -> bundle.putString(t, u.toString())
                }
            }
            bundle
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}