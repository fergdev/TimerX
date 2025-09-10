package com.intervallum.util

inline fun <reified T : S, S> S.letType(block: T.() -> Unit) {
    (this as? T)?.let { block(it) }
}
