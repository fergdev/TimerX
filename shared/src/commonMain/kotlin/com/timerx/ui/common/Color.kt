package com.timerx.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.timerx.ui.theme.Opacity

private const val HALF_LUMINANCE = 0.5F
fun Color.contrastColor() = if (isColorDark(this)) Color.White else Color.Black

fun isColorDark(color: Color) = color.luminance() < HALF_LUMINANCE

fun Color.lightDisplayColor() = this.copy(alpha = .25f)

fun Color.Companion.random() = rainbow.random()

@Composable
fun rememberRandomColor() = remember { Color.random() }

@Composable
fun containerColor(): Color =
    MaterialTheme.colorScheme.surface.copy(alpha = Opacity.secondary)

val magenta = Color(0xffd500f9)
val violet = Color(0xff651fff)
val indigo = Color(0xff3d5afe)
val blue = Color(0xff2979ff)
val azure = Color(0xff00b0ff)
val cyan = Color(0xff00e5ff)
val teal = Color(0xff1de9b6)
val green = Color(0xff00e676)
val light_green = Color(0xff76ff03)
val lime = Color(0xffc6ff00)
val yellow = Color(0xffffea00)
val amber = Color(0xffffc400)
val orange = Color(0xffff9100)
val deep_orange = Color(0xffff3d00)
val red = Color(0xffff1744)
val pink = Color(0xffff4081)
val gray = Color(0xff607d8b)
val brown = Color(0xff8d6e63)

val rainbow = listOf(
    magenta,
    violet,
    indigo,
    blue,
    azure,
    cyan,
    teal,
    green,
    light_green,
    lime,
    yellow,
    amber,
    orange,
    deep_orange,
    red,
    pink,
    gray,
    brown
)
