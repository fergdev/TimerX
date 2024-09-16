package com.timerx.database

import com.timerx.domain.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant

object InMemoryTimerXRepository : ITimerRepository {
    private val flow = MutableStateFlow<List<Timer>>(emptyList())
    override fun getShallowTimers(): Flow<List<Timer>> {
        return flow
    }

    override suspend fun insertTimer(timer: Timer) {
        flow.update {
            val maxId = flow.value.maxOfOrNull { it.id }  ?: 0L
            it + timer.copy(id = maxId + 1)
        }
    }

    override suspend fun updateTimerStats(
        timerId: Long,
        startedCount: Long,
        completedCount: Long,
        lastRun: Instant
    ) {
    }

    override suspend fun updateTimer(timer: Timer) {
    }

    override suspend fun deleteTimer(timerId: Long) {
        flow.update {
            it.filter { it.id == timerId }
        }
    }

    override suspend fun duplicate(timerId: Long) {
    }

    override suspend fun getTimer(timerId: Long): Flow<Timer?> {
        return flow { emit(flow.value.firstOrNull { it.id == timerId }) }
    }

    override suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    ) {
    }

    override suspend fun doesTimerExist(timerId: Long): Flow<Boolean> {
        return flow {}
    }

}
