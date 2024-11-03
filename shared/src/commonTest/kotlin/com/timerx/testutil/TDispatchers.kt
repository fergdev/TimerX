package com.timerx.testutil

import com.timerx.coroutines.TxDispatchers
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher

fun TxDispatchers.idleAll() {
    main.testCoroutineScheduler.advanceUntilIdle()
    io.testCoroutineScheduler.advanceUntilIdle()
    default.testCoroutineScheduler.advanceUntilIdle()
}

fun testDispatchers() =
    TxDispatchers(
        main = StandardTestDispatcher(),
        io = StandardTestDispatcher(),
        default = StandardTestDispatcher(),
    )
