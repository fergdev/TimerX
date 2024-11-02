package com.timerx.testutil

import com.timerx.coroutines.TDispatchers
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher

fun TDispatchers.idleAll() {
    main.testCoroutineScheduler.advanceUntilIdle()
    io.testCoroutineScheduler.advanceUntilIdle()
    default.testCoroutineScheduler.advanceUntilIdle()
}

fun testDispatchers() =
    TDispatchers(
        main = StandardTestDispatcher(),
        io = StandardTestDispatcher(),
        default = StandardTestDispatcher(),
    )
