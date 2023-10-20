package com.timerx.domain

data class Timer(
    val id: Int,
    val name: String,
    val intervalSets: List<IntervalSet>
)

data class IntervalSet(
    val id: Int,
    val repetitions: Int,
    val intervals: List<Interval>
)

data class Interval(
    val id: Int,
    val name: String,
    val duration: Int
)
