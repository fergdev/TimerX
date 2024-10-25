package com.timerx.sound

import com.timerx.util.assert

data class Volume(val value: Float) {
    init { assert(value in range) { "Invalid volume: $value" } }
    companion object {
        val range = 0.0f..1.0f
        val default = Volume(1.0f)
    }
}
