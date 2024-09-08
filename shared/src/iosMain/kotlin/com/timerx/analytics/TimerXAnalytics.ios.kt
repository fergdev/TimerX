package com.timerx.analytics

class TimerXAnalytics : ITimerXAnalytics {
    override fun logEvent(
        eventName: String,
        params: Map<String, Any>?
    ) {
        firebaseIosCallback?.logEvent(eventName, params.toString())
    }

    private fun Map<String, Any>?.toString(): String {
        if (this == null) return ""

        val sb = StringBuilder()
        this.forEach {
            sb.append("${it.key}:${it.value},")
        }
        return sb.toString()
    }
}

actual fun getTimerXAnalytics(): ITimerXAnalytics = TimerXAnalytics()

interface FirebaseIosCallback {
    fun logEvent(eventId: String, params: String)
}

private var firebaseIosCallback: FirebaseIosCallback? = null

// Callback from swift that allows settings of firebase callback
@Suppress("unused")
fun firebaseCallback(callback: FirebaseIosCallback) {
    firebaseIosCallback = callback
}
