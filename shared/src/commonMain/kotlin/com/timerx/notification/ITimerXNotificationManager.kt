package com.timerx.notification

import com.timerx.timermanager.TimerEvent

interface ITimerXNotificationManager {

    fun start()

    fun stop()

    fun updateNotification(timerEvent: TimerEvent)
}
