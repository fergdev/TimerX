package com.intervallum.database

import com.intervallum.domain.ShallowTimer
import com.intervallum.domain.Timer
import com.intervallum.util.mapIfNull
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.plus
import io.github.xxfast.kstore.storage.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class KStoreDatabase : ITimerRepository {
    private val store: KStore<List<Timer>> = storeOf("intervallum.db", default = emptyList())
    private var nextTimerId = 0L

    override fun getShallowTimers(): Flow<List<ShallowTimer>> =
        store.updates.mapIfNull(emptyList())
            .map { timers ->
                timers.map { timer ->
                    ShallowTimer(
                        id = timer.id,
                        sortOrder = timer.sortOrder,
                        name = timer.name,
                        duration = timer.duration,
                        startedCount = timer.startedCount,
                        completedCount = timer.completedCount,
                        createdAt = timer.createdAt,
                        lastRun = timer.lastRun
                    )
                }
            }
            .onEach {
                nextTimerId = it.maxOfOrNull { timer ->
                    timer.id
                }?.plus(1L) ?: 0L
            }

    override suspend fun insertTimer(timer: Timer): Long {
        store.plus(timer.prepareForInsert())
        return nextTimerId
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun incrementStartedCount(timerId: Long) {
        store.update { timers ->
            val index = timers?.indexOfFirst { it.id == timerId } ?: return@update null
            val updatedTimer = with(timers[index]) {
                copy(
                    startedCount = startedCount + 1,
                    lastRun = Clock.System.now()
                )
            }
            val toMutableList = timers.toMutableList()
            toMutableList[index] = updatedTimer
            return@update toMutableList
        }
    }

    override suspend fun incrementCompletedCount(timerId: Long) {
        store.update { timers ->
            val index = timers?.indexOfFirst { it.id == timerId } ?: return@update null
            val updatedTimer = with(timers[index]) {
                copy(startedCount = completedCount + 1)
            }
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

    override suspend fun duplicate(timerId: Long): Long {
        val id = nextTimerId
        store.update { timerList ->
            val toDuplicate = timerList?.first { timer ->
                timer.id == timerId
            }
            requireNotNull(toDuplicate) { "Timer to duplicate not found $timerId" }
            timerList.plus(
                toDuplicate.copy(
                    id = id,
                    name = toDuplicate.name + " (copy)",
                )
            )
        }
        return id
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
