package com.timerx.notification

import com.timerx.domain.TimerEvent

expect fun getTimerXNotificationManager(): ITimerXNotificationManager

interface ITimerXNotificationManager {

    fun start()

    fun stop()

    fun updateNotification(timerEvent: TimerEvent)
}
