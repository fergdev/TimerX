package com.timerx.util

import androidx.compose.runtime.Composable
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

@Composable
fun <T> T?.composeLet(block: @Composable (T) -> Unit): (@Composable () -> Unit)? =
    this?.let {
        @Composable {
            block(this)
        }
    }
