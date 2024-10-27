package com.timerx.settings

data class BackgroundAlpha(val value: Float) {
    init {
        require(value in range) {
            "Alpha $value must be between $range"
        }
    }

    companion object {
        val range = 0.0f..0.99f
        val default = BackgroundAlpha(0.2f)
    }
}
