package com.timerx.repository

import com.timerx.domain.TimerInterval
import com.timerx.domain.Timer
import com.timerx.domain.TimerSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

interface TimerRepository {
    fun timers(): PersistentList<Timer>

    class TimerRepositoryImpl : TimerRepository {

        override fun timers() = persistentListOf(
            Timer(
                id = 0,
                name = "EMOM",
                sets = listOf(
                    TimerSet(
                        id = 0,
                        repetitions = 1,
                        intervals = listOf(TimerInterval(name = "Warmup", duration = 10))
                    ),
                    TimerSet(
                        id = 0,
                        repetitions = 20,
                        intervals = listOf(TimerInterval(name = "Work", duration = 60))
                    )
                )
            ),
            Timer(
                id = 1,
                name = "30 for 30",
                sets = listOf(
                    TimerSet(
                        id = 0,
                        repetitions = 10,
                        intervals = listOf(
                            TimerInterval(name = "Work", duration = 30),
                            TimerInterval(name = "Rest", duration = 30)
                        )
                    )
                )
            )
        )
    }
}

expect fun getTimerRepository(): TimerRepository
