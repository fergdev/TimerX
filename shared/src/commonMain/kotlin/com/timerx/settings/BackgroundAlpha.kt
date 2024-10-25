package com.timerx.settings

import com.timerx.util.assert

val backgroundAlphaRange = 0.0f..0.99f
data class BackgroundAlpha(val value: Float) {
    init {
        assert(value in backgroundAlphaRange) {
            "Alpha $value must be between $backgroundAlphaRange"
        }
    }

    companion object {
        val default = BackgroundAlpha(0.2f)
    }
}
