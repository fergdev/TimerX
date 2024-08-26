package com.timerx.notification

expect fun getTimerXNotificationManager(): ITimerXNotificationManager

interface ITimerXNotificationManager {

    fun start()

    fun stop()

    fun updateNotification(isRunning: Boolean, info: String, backgroundColor: Int)
}