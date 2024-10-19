package com.timerx.database

import com.timerx.domain.Timer
import com.timerx.util.mapIfNull
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.plus
import io.github.xxfast.kstore.storage.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant

class KStoreDatabase : ITimerRepository {
    private val store: KStore<List<Timer>> = storeOf("timerX.db", default = emptyList())

    private var nextTimerId = 0L

    override fun getShallowTimers(): Flow<List<Timer>> =
        store.updates.mapIfNull(emptyList()).onEach {
            nextTimerId = it.maxOfOrNull { timer ->
                timer.id
            }?.plus(1L) ?: 0L
        }

    override suspend fun insertTimer(timer: Timer): Long {
        store.plus(timer.prepareForInsert())
        return nextTimerId
    }

    override suspend fun updateTimerStats(
        timerId: Long,
        startedCount: Long,
        completedCount: Long,
        lastRun: Instant
    ) {
        store.update { timers ->
            val index = timers?.indexOfFirst { it.id == timerId } ?: return@update null
            val updatedTimer = timers[index].copy(
                startedCount = startedCount,
                completedCount = completedCount,
                lastRun = lastRun
            )
            val toMutableList = timers.toMutableList()
            toMutableList[index] = updatedTimer
            return@update toMutableList
        }
    }

    override suspend fun updateTimer(timer: Timer) {
        store.update { timers ->
            val index = timers?.indexOfFirst { it.id == timer.id } ?: return@update null
            val toMutableList = timers.toMutableList()
            toMutableList[index] = timer.prepareForInsert()
            return@update toMutableList
        }
    }

    override suspend fun deleteTimer(timerId: Long) =
        store.update {
            it?.filter { timer -> timer.id != timerId }
        }

    override suspend fun duplicate(timerId: Long) {
        store.update { timerList ->
            val toDuplicate = timerList?.first { timer ->
                timer.id == timerId
            }
            toDuplicate?.let { timerList.plus(it.copy(id = nextTimerId)) }
        }
    }

    override suspend fun getTimer(timerId: Long): Flow<Timer?> =
        store.updates.map {
            it?.firstOrNull { timer ->
                timer.id == timerId
            }
        }

    override suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    ) {
        store.update { timers ->
            timers ?: return@update null
            val fromTimer = timers[fromId.toInt()]
            val toTimer = timers[toId.toInt()]
            val toMutableList = timers.toMutableList()
            toMutableList[toId.toInt()] = fromTimer
            toMutableList[fromId.toInt()] = toTimer
            return@update toMutableList
        }
    }

    override suspend fun doesTimerExist(timerId: Long): Flow<Boolean> =
        store.updates.map { it?.firstOrNull { timer -> timer.id == timerId } != null }

    private fun Timer.prepareForInsert(): Timer {
        var setId = 0L
        return this.copy(
            id = nextTimerId,
            sets = this.sets.map { set ->
                set.copy(
                    id = setId++
                )
            }
        )
    }
}
