package com.timerx.util

inline fun assert(predicate: Boolean, message: () -> String) {
    if (predicate) throw IllegalStateException(message())
}

inline fun assertNotNull(obj: Any?, message: () -> String) {
    kotlin.contracts.contract {
        returns() implies (obj != null)
    }
    if (obj == null) throw IllegalStateException(message())
}
