package com.intervallum.sound

data class Volume(val value: Float) {
    init { require(value in range) { "Invalid volume: $value" } }
    companion object {
        val range = 0.0f..1.0f
        val default = Volume(1.0f)
    }
}
