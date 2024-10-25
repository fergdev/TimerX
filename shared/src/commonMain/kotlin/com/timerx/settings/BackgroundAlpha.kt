package com.timerx.settings

import com.timerx.util.assert

data class BackgroundAlpha(val value: Float) {
    init {
        assert(value in range) {
            "Alpha $value must be between $range"
        }
    }

    companion object {
        val range = 0.0f..0.99f
        val default = BackgroundAlpha(0.2f)
    }
}
