package com.timerx

import com.timerx.domain.Timer
import com.timerx.repository.getTimerRepository

class Timers {
    private val timerRepository = getTimerRepository()

    fun timers(): List<Timer> {
        return timerRepository.timers
    }
}