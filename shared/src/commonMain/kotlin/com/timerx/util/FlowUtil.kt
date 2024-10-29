package com.timerx.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> Flow<T?>.mapIfNull(t: T): Flow<T> =
    this.map {
        it ?: t
    }
