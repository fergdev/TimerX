package com.timerx.notification

import com.timerx.domain.TimerEvent

interface ITimerXNotificationManager {

    fun start()

    fun stop()

    fun updateNotification(timerEvent: TimerEvent)
}
