package com.timerx.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> Flow<T?>.mapIfNull(t: T): Flow<T> =
    this.map {
        it ?: t
    }

fun String.capitalize(): String =
    this.replaceFirstChar { name ->
        if (name.isLowerCase()) name.uppercaseChar()
        else name
    }

fun <T> Boolean.ifTake(t: T): T? = if (this) t else null

@Composable
fun <T> Collection<T>.withForEach(block: @Composable T.() -> Unit) {
    forEach {
        with(it) {
            block()
        }
    }
}

fun <T> Modifier.letThen(obj: T?, block: Modifier.(T) -> Modifier): Modifier =
    if (obj != null) this.then(block(obj))
    else Modifier
