package com.timerx.di

import com.timerx.database.ITimerRepository
import com.timerx.domain.SortTimersBy
import com.timerx.domain.Timer
import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import org.koin.dsl.module

actual val platformModule = module {
    single<ITimerXSettings> { Settings }
    single<ITimerRepository> { Database }

}
object Settings : ITimerXSettings {
    override val settings: Flow<com.timerx.settings.Settings>
        get() = flow {  }

    override suspend fun setVolume(volume: Float) {
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
    }

    override suspend fun setIgnoreNotificationPermissions() {
    }

    override suspend fun setSortTimersBy(sortTimersBy: SortTimersBy) {
    }
}

object Database : ITimerRepository {
    override fun getShallowTimers(): Flow<List<Timer>> {
        return flow {  }
    }

    override suspend fun insertTimer(timer: Timer) {
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
    }

    override suspend fun duplicate(timerId: Long) {
    }

    override suspend fun getTimer(timerId: Long): Flow<Timer> {
        return flow {  }
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
