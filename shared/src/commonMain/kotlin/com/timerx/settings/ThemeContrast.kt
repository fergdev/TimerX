package com.timerx.settings

data class ThemeContrast(val value: Double) {
    init {
        require(value in range) {
            "ThemeContrast $value must be between -1.0 and 1.0"
        }
    }

    companion object {
        val range = -1.0f..1.0f
        val default = ThemeContrast(0.0)
    }
}
