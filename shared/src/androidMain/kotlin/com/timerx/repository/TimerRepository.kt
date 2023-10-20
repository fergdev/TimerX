package com.timerx.repository

import com.timerx.domain.Timer

class AndroidTimerRepository : TimerRepository {
    override val timers: List<Timer>
        get() = wow()
}

actual fun getTimerRepository(): TimerRepository = AndroidTimerRepository()
