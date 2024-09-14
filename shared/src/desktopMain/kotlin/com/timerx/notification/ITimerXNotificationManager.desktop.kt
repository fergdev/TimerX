package com.timerx.notification

import com.timerx.domain.TimerEvent

actual fun getTimerXNotificationManager() = object : ITimerXNotificationManager {
    override fun start() {

    }

    override fun stop() {
    }

    override fun updateNotification(timerEvent: TimerEvent) {
    }
}