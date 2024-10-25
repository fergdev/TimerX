package com.timerx.settings

import com.timerx.util.assert

data class ThemeContrast(val value: Double) {
    init {
        assert(value in range) {
            "ThemeContrast $value must be between -1.0 and 1.0"
        }
    }

    companion object {
        val range = -1.0f..1.0f
        val default = ThemeContrast(0.0)
    }
}
