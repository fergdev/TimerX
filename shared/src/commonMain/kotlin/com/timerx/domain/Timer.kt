package com.timerx.domain

data class Timer(
    val id: Long = -1,
    val name: String,
    val sets: List<TimerSet>
)

data class TimerSet(
    val id: Long = -1,
    val repetitions: Long,
    val intervals: List<TimerInterval>
)

data class TimerInterval(
    val id: Long = -1,
    val name: String,
    val duration: Long
)
