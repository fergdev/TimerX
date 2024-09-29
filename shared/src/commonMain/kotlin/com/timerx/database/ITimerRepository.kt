package com.timerx.database

import com.timerx.domain.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface ITimerRepository {
    fun getShallowTimers(): Flow<List<Timer>>
    suspend fun insertTimer(timer: Timer)
    suspend fun updateTimerStats(
        timerId: Long,
        startedCount: Long,
        completedCount: Long,
        lastRun: Instant
    )

    suspend fun updateTimer(timer: Timer)
    suspend fun deleteTimer(timerId: Long)
    suspend fun duplicate(timerId: Long)
    suspend fun getTimer(timerId: Long): Flow<Timer?>
    suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    )

    suspend fun doesTimerExist(timerId: Long): Flow<Boolean>
}
