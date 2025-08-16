package com.intervallum.domain

fun Timer.length() = sets.fold(0L) { acc, timerSet ->
    acc + timerSet.length()
}

fun List<TimerSet>.length() =
    fold(0L) { acc, timerSet ->
        acc + timerSet.length()
    }

fun TimerSet.length() =
    intervals.fold(0L) { acc, interval ->
        acc + interval.length()
    } * repetitions

fun TimerInterval.length() = duration
