package com.timerx.analytics

actual class TimerXAnalytics actual constructor() {
    actual fun logEvent(
        eventName: String,
        params: Map<String, Any>?
    ) {
        cb?.logEvent(eventName, params.toString())
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

interface FirebaseIosCallback {
    fun logEvent(eventId: String, params: String)
}

var cb: FirebaseIosCallback? = null
fun firebaseCallback(callback: FirebaseIosCallback) {
    cb = callback
}