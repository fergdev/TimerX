package com.timerx.repository

import com.timerx.domain.Timer

class IOSTimerRepository : TimerRepository {
    override val timers: List<Timer>
        get() = wow()
}

actual fun getTimerRepository(): TimerRepository = IOSTimerRepository()