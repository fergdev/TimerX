package com.timerx.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.timerx.util.isValid

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

@Composable
fun <T> Collection<T>.withForEach(block: @Composable T.() -> Unit) {
    forEach {
        with(it) {
            block()
        }
    }
}

@Composable
fun <T> T?.composeLet(block: @Composable (T) -> Unit): (@Composable () -> Unit)? =
    this?.let {
        @Composable {
            block(this)
        }
    }

inline fun Modifier.thenIf(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
) = then(if (condition) modifier(Modifier) else Modifier)

inline fun <T> Modifier.thenLet(obj: T?, block: Modifier.(T) -> Modifier): Modifier =
    if (obj != null) then(block(Modifier, obj))
    else this
