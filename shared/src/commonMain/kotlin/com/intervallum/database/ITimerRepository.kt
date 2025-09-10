package com.intervallum.database

import com.intervallum.domain.ShallowTimer
import com.intervallum.domain.Timer
import kotlinx.coroutines.flow.Flow

@Suppress("ComplexInterface")
interface ITimerRepository {
    fun getShallowTimers(): Flow<List<ShallowTimer>>
    suspend fun insertTimer(timer: Timer): Long

    suspend fun incrementStartedCount(timerId: Long)
    suspend fun incrementCompletedCount(timerId: Long)
    suspend fun updateTimer(timer: Timer)
    suspend fun deleteTimer(timerId: Long)
    suspend fun duplicate(timerId: Long): Long
    suspend fun getTimer(timerId: Long): Flow<Timer?>
    suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    )

    suspend fun doesTimerExist(timerId: Long): Flow<Boolean>
}
