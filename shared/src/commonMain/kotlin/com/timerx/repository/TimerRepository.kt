package com.timerx.repository

import com.timerx.domain.Interval
import com.timerx.domain.Timer
import com.timerx.domain.IntervalSet

interface TimerRepository {
    val timers: List<Timer>

    fun wow() = listOf(
        Timer(
            id = 0,
            name = "EMOM",
            intervalSets = listOf(
                IntervalSet(
                    id = 0,
                    repetitions = 1,
                    intervals = listOf(Interval(0, "Warmup", 10))
                ),
                IntervalSet(
                    id = 0,
                    repetitions = 1,
                    intervals = listOf(Interval(0, "Work", 60))
                )
            )
        ),
        Timer(
            id = 1,
            name = "30 for 30",
            intervalSets = listOf(
                IntervalSet(
                    id = 0,
                    repetitions = 10,
                    intervals = listOf(
                        Interval(0, "Work", 30),
                        Interval(0, "Rest", 30)
                    )
                )
            )
        )
    )
}

expect fun getTimerRepository(): TimerRepository
