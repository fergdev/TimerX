package com.timerx.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun String.branded(color: Color = MaterialTheme.colorScheme.primary) = buildAnnotatedString {
    when {
        !isValid() -> return@buildAnnotatedString
        !first().isLetterOrDigit() -> return AnnotatedString(this@branded)
        else -> {
            withStyle(style = SpanStyle(color = color)) { append(first()) }
            append(drop(1))
        }
    }
}

@Composable
fun String.doubleBranded(color: Color = MaterialTheme.colorScheme.primary) = buildAnnotatedString {
    when {
        !isValid() -> return@buildAnnotatedString
        !first().isLetterOrDigit() -> return AnnotatedString(this@doubleBranded)
        else -> {
            withStyle(style = SpanStyle(color = color)) { append(first()) }
            append(drop(1).dropLast(1))
            withStyle(style = SpanStyle(color = color)) { append(last()) }
        }
    }
}

fun AnnotatedString.Builder.appendNewline(repeat: Int = 1) {
    repeat(repeat) {
        append("\n")
    }
}

fun String?.isValid(): Boolean {
    kotlin.contracts.contract {
        returns(true) implies (this@isValid != null)
    }
    return !isNullOrBlank() && !equals("null", true)
}

fun String?.takeIfValid(): String? = if (isValid()) this else null

inline fun Modifier.thenIf(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
) = then(if (condition) modifier(Modifier) else Modifier)

inline fun <T> Modifier.thenLet(obj: T?, block: Modifier.(T) -> Modifier): Modifier =
    if (obj != null) then(block(Modifier, obj))
    else this

enum class FadingEdge {
    Start, End, Top, Bottom
}

@Suppress("MagicNumber")
fun Modifier.fadingEdge(
    fadingEdge: FadingEdge,
    size: Dp,
    rtlAware: Boolean = false,
) = composed {
    val direction = LocalLayoutDirection.current
    val invert = direction == LayoutDirection.Rtl && rtlAware
    val edge = when (fadingEdge) {
        FadingEdge.Top, FadingEdge.Bottom -> fadingEdge
        FadingEdge.Start -> if (invert) FadingEdge.End else FadingEdge.Start
        FadingEdge.End -> if (invert) FadingEdge.Start else FadingEdge.End
    }
    graphicsLayer { alpha = 0.99f }.drawWithCache {
        val colors = listOf(Color.Transparent, Color.Black)
        val sizePx = size.toPx()
        val brush = when (edge) {
            FadingEdge.Start -> Brush.horizontalGradient(colors, startX = 0f, endX = sizePx)
            FadingEdge.End -> Brush.horizontalGradient(
                colors.reversed(),
                startX = this.size.width - sizePx,
                endX = this.size.width
            )

            FadingEdge.Top -> Brush.verticalGradient(colors, startY = 0f, endY = sizePx)
            FadingEdge.Bottom -> Brush.verticalGradient(
                colors.reversed(),
                startY = this.size.height - sizePx,
                endY = this.size.height
            )
        }
        onDrawWithContent {
            drawContent()
            drawRect(
                brush = brush,
                blendMode = BlendMode.DstIn
            )
        }
    }
}
