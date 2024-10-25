package com.timerx.sound

import com.timerx.util.assert

val volumeRange = 0.0f..1.0f
data class Volume(val value: Float) {
    init { assert(value in volumeRange) { "Invalid volume: $value" } }
    companion object {
        val default = Volume(1.0f)
    }
}
