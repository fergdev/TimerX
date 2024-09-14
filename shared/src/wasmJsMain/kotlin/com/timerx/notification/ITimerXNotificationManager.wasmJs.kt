package com.timerx.notification

import com.timerx.domain.TimerEvent

actual fun getTimerXNotificationManager()= object: ITimerXNotificationManager {
    override fun start() {
        println("start notification manager")
    }

    override fun stop() {
        println("stop notification manager")
    }

    override fun updateNotification(timerEvent: TimerEvent) {
        println("update notification manager")
    }

}