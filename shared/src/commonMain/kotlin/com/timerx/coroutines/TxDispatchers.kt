package com.timerx.coroutines

import kotlinx.coroutines.CoroutineDispatcher

@Suppress("UseDataClass")
class TxDispatchers(
    val default: CoroutineDispatcher,
    val main: CoroutineDispatcher,
    val io: CoroutineDispatcher
)
