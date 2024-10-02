package com.timerx.util

import com.timerx.ui.common.isValid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A middle point of the range. The value is rounded down.
 */
val ClosedRange<Float>.midpoint: Float get() = start / 2 + endInclusive / 2

fun Float?.takeIfNotZero(): Float? = takeIf { it != 0.0f }

fun <T> Flow<T?>.mapIfNull(t: T): Flow<T> =
    this.map {
        it ?: t
    }

public fun String?.takeIfValid(): String? = if (isValid()) this else null

fun String.capitalize(): String =
    this.replaceFirstChar { name ->
        if (name.isLowerCase()) name.uppercaseChar()
        else name
    }

fun <T> Boolean.ifTake(t: T): T? = if (this) t else null