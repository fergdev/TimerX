package com.timerx.repository

import com.timerx.domain.Timer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

class IOSTimerRepository : TimerRepository {

    override fun timers(): PersistentList<Timer> {
        return persistentListOf()
    }
}

actual fun getTimerRepository(): TimerRepository = IOSTimerRepository()