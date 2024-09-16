package com.timerx.util


/**
 * A middle point of the range. The value is rounded down.
 */
public val ClosedRange<Float>.midpoint: Float get() = start / 2 + endInclusive / 2

public fun Float?.takeIfNotZero(): Float? = takeIf { it != 0.0f }
