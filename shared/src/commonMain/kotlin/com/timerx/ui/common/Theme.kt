package com.timerx.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val COLOR_PRIMARY_DARK = 0xFFBB86FCL
private const val COLOR_SECONDARY_DARK = 0xFF03DAC5L
private const val COLOR_TERTIARY_DARK = 0xFF3700B3L
private const val COLOR_PRIMARY_LIGHT = 0xFF6200EEL
private const val COLOR_SECONDARY_LIGHT = 0xFF6200EEL
private const val COLOR_TERTIARY_LIGHT = 0xFF6200EEL

val darkColorScheme = darkColorScheme(
    primary = Color(COLOR_PRIMARY_DARK),
    secondary = Color(COLOR_SECONDARY_DARK),
    tertiary = Color(COLOR_TERTIARY_DARK)
)

val lightColorScheme = lightColorScheme(
    primary = Color(COLOR_PRIMARY_LIGHT),
    secondary = Color(COLOR_SECONDARY_LIGHT),
    tertiary = Color(COLOR_TERTIARY_LIGHT)
)

@Composable
fun TimerXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme
    } else {
        lightColorScheme
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
