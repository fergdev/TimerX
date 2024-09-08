package com.timerx.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private const val HALF_LUMINANCE = 0.5F
fun Color.contrastColor() = if (isColorDark(this)) Color.White else Color.Black

fun isColorDark(color: Color) = color.luminance() < HALF_LUMINANCE

fun Color.lightDisplayColor() = this.copy(alpha = .25f)
