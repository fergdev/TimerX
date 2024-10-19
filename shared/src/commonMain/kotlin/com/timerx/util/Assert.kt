package com.timerx.util

inline fun assert(predicate: Boolean, message: () -> String) {
    if (predicate) throw IllegalStateException(message())
}
