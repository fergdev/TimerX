package com.timerx.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private const val HALF_LUMINANCE = 0.5F
fun Color.contrastColor(): Color {
    return if (isColorDark(this)) Color.White else Color.Black
}

fun isColorDark(color: Color): Boolean {
    return color.luminance() < HALF_LUMINANCE
}

fun Color.lightDisplayColor(): Color {
    return this.copy(alpha = .25f)
}
