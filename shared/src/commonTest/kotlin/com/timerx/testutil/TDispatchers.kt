package com.timerx.testutil

import com.timerx.coroutines.TxDispatchers
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

fun TxDispatchers.idle() {
    main.testCoroutineScheduler.advanceUntilIdle()
    io.testCoroutineScheduler.advanceUntilIdle()
    default.testCoroutineScheduler.advanceUntilIdle()
}

fun TxDispatchers.mainIdle() = main.testCoroutineScheduler.advanceUntilIdle()
fun TxDispatchers.ioIdle() = io.testCoroutineScheduler.advanceUntilIdle()
fun TxDispatchers.defaultIdle() = default.testCoroutineScheduler.advanceUntilIdle()

fun testDispatchers(testScheduler: TestCoroutineScheduler? = null) =
    TxDispatchers(
        main = StandardTestDispatcher(testScheduler),
        io = StandardTestDispatcher(testScheduler),
        default = StandardTestDispatcher(testScheduler),
    )
