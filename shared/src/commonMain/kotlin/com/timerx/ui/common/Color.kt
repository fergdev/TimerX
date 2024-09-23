package com.timerx.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private const val HALF_LUMINANCE = 0.5F
fun Color.contrastColor() = if (isColorDark(this)) Color.White else Color.Black

fun isColorDark(color: Color) = color.luminance() < HALF_LUMINANCE

fun Color.lightDisplayColor() = this.copy(alpha = .25f)

fun Color.Companion.random() = rainbow.random()

@Composable
fun rememberRandomColor() = remember { Color.random() }

val magenta = Color(0xffd500f9) // -2817799
val violet = Color(0xff651fff) // -10149889
val indigo = Color(0xff3d5afe) // -12756226
val blue = Color(0xff2979ff) // -14059009
val azure = Color(0xff00b0ff) // -16731905
val cyan = Color(0xff00e5ff) // -16718337
val teal = Color(0xff1de9b6) // -14816842
val green = Color(0xff00e676) // -16718218
val light_green = Color(0xff76ff03) // -8978685
val lime = Color(0xffc6ff00) // -3735808
val yellow = Color(0xffffea00) // -5632
val amber = Color(0xffffc400) // -15360
val orange = Color(0xffff9100) // -28416
val deep_orange = Color(0xffff3d00) // -49920
val red = Color(0xffff1744) // -59580
val pink = Color(0xffff4081) // -49023
val gray = Color(0xff607d8b) // -10453621
val brown = Color(0xff8d6e63) // -7508381

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
